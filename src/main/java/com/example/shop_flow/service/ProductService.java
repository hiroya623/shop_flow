
package com.example.shop_flow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.shop_flow.entity.Product;
import com.example.shop_flow.repository.ProductRepository;

/**
 * ShopFlowの商品管理に関する業務処理を担当するサービス。
 *
 * 商品の登録・更新・削除・検索を行う。
 * コントローラーからデータベース操作の詳細を分離し、
 * 商品情報の入力チェックや在庫数の検証を一元管理する。
 */
@Service
public class ProductService {

    /** 商品データの検索・保存・削除を行うリポジトリ。 */
    private final ProductRepository productRepository;

    /**
     * 商品リポジトリを受け取り、サービスを初期化する。
     *
     * @param productRepository 商品データを操作するリポジトリ
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * すべての商品を取得する。
     *
     * @return データベースに登録されている商品一覧
     */
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        // 商品データをすべて取得して呼び出し元へ返す。
        return productRepository.findAll();
    }

    /**
     * 商品IDを指定して商品を取得する。
     *
     * @param id 検索対象の商品ID
     * @return 該当する商品
     * @throws IllegalArgumentException 商品IDが存在しない場合
     */
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        // 商品が存在しない場合は、理由が分かる例外を返す。
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("指定された商品が見つかりません。"));
    }

    /**
     * 商品名の一部を指定して商品を検索する。
     *
     * @param name 検索する商品名の一部
     * @return 条件に一致した商品一覧
     */
    @Transactional(readOnly = true)
    public List<Product> searchByName(String name) {
        // 検索文字列が未入力の場合は全商品を返す。
        if (!StringUtils.hasText(name)) {
            return productRepository.findAll();
        }

        // 前後の空白を取り除いて商品名の部分一致検索を行う。
        return productRepository.findByNameContaining(name.trim());
    }

    /**
     * 指定した在庫数以下の商品を取得する。
     *
     * @param stockQuantity 在庫数の上限
     * @return 条件に一致した商品一覧
     * @throws IllegalArgumentException 在庫数がnullまたは負数の場合
     */
    @Transactional(readOnly = true)
    public List<Product> findByStockQuantityLessThanEqual(Integer stockQuantity) {
        // 在庫数の検索条件が不正な場合は処理を中止する。
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException(
                    "検索する在庫数は0以上で指定してください。");
        }

        // 指定した在庫数以下の商品を検索する。
        return productRepository.findByStockQuantityLessThanEqual(stockQuantity);
    }

    /**
     * 新しい商品を登録する。
     *
     * @param product 登録する商品情報
     * @return データベースに保存された商品
     * @throws IllegalArgumentException 商品情報や価格・在庫数が不正な場合
     */
    @Transactional
    public Product create(Product product) {
        // 商品情報が渡されていない場合は登録しない。
        validateProduct(product);

        // 新規登録時は在庫数が未指定なら0に設定する。
        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }

        // 新規登録では既存商品のIDを引き継がない。
        product.setId(null);

        // 入力チェック後の商品情報をデータベースへ保存する。
        return productRepository.save(product);
    }

    /**
     * 登録済み商品の情報を更新する。
     *
     * @param id 更新対象の商品ID
     * @param product 更新する商品情報
     * @return 更新後の商品
     * @throws IllegalArgumentException 商品が存在しない場合や入力が不正な場合
     */
    @Transactional
    public Product update(Long id, Product product) {
        // 更新対象の商品が存在することを確認する。
        Product existingProduct = findById(id);

        // 更新内容が不正でないことを確認する。
        validateProduct(product);

        // 画面から送られたIDではなく、URLで指定されたIDを使用する。
        existingProduct.setName(product.getName().trim());
        existingProduct.setDescription(normalizeDescription(
                product.getDescription()));
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());

        // 既存の商品情報を更新して保存する。
        return productRepository.save(existingProduct);
    }

    /**
     * 指定した商品の在庫数を更新する。
     *
     * @param id 在庫を変更する商品ID
     * @param stockQuantity 変更後の在庫数
     * @return 在庫更新後の商品
     * @throws IllegalArgumentException 商品が存在しない場合や在庫数が不正な場合
     */
    @Transactional
    public Product updateStock(Long id, Integer stockQuantity) {
        // 在庫数の未入力とマイナス値を防ぐ。
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException(
                    "在庫数は0以上で入力してください。");
        }

        // 対象商品を取得し、在庫数だけを更新する。
        Product product = findById(id);
        product.setStockQuantity(stockQuantity);

        // 更新結果をデータベースへ保存する。
        return productRepository.save(product);
    }

    /**
     * 指定した商品を削除する。
     *
     * @param id 削除対象の商品ID
     * @throws IllegalArgumentException 商品IDが存在しない場合
     */
    @Transactional
    public void delete(Long id) {
        // 存在しない商品を誤って削除扱いにしないよう事前確認する。
        Product product = findById(id);

        // 対象商品のデータをデータベースから削除する。
        productRepository.delete(product);
    }

    /**
     * 商品情報の必須項目と数値を検証する。
     *
     * @param product 検証対象の商品情報
     * @throws IllegalArgumentException 商品名、価格、在庫数などが不正な場合
     */
    private void validateProduct(Product product) {
        // 商品そのものが渡されていない場合は処理できない。
        if (product == null) {
            throw new IllegalArgumentException("商品情報を入力してください。");
        }

        // 商品名の未入力と文字数超過を防ぐ。
        if (!StringUtils.hasText(product.getName())) {
            throw new IllegalArgumentException("商品名を入力してください。");
        }
        if (product.getName().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "商品名は100文字以内で入力してください。");
        }

        // 商品説明の文字数超過を防ぐ。
        if (product.getDescription() != null
                && product.getDescription().length() > 1000) {
            throw new IllegalArgumentException(
                    "商品説明は1000文字以内で入力してください。");
        }

        // 価格が未入力、または負数の場合は登録・更新しない。
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new IllegalArgumentException(
                    "価格は0以上で入力してください。");
        }

        // 在庫数が未入力の場合は、新規登録時のみ0を許可する。
        // 更新処理では在庫数が明示されている必要がある。
        if (product.getStockQuantity() == null
                || product.getStockQuantity() < 0) {
            throw new IllegalArgumentException(
                    "在庫数は0以上で入力してください。");
        }
    }

    /**
     * 商品説明の前後の空白を取り除き、空欄をnullに統一する。
     *
     * @param description 入力された商品説明
     * @return 整形後の商品説明。空欄の場合はnull
     */
    private String normalizeDescription(String description) {
        // 説明が未入力ならnullとして扱う。
        if (!StringUtils.hasText(description)) {
            return null;
        }

        // 前後の空白を除去した説明を返す。
        return description.trim();
    }
}
