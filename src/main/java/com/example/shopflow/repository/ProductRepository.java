
package com.example.shopflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shopflow.entity.Product;

/**
 * ShopFlowの商品データをデータベースから検索・保存するリポジトリ。
 *
 * JpaRepositoryを継承することで、商品の登録・更新・削除・検索などの
 * 基本的なデータベース操作を利用できる。
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 商品名に指定した文字列が含まれる商品を検索する。
     *
     * 商品名の一部による検索を行い、商品管理画面の検索機能などで利用する。
     * 大文字・小文字の区別などは、データベースの照合順序に依存する。
     *
     * @param name 検索する商品名の一部
     * @return 条件に一致した商品の一覧
     */
    List<Product> findByNameContaining(String name);

    /**
     * 在庫数が指定した値以下の商品を検索する。
     *
     * 在庫が少ない商品や、在庫切れの商品を確認する処理に利用できる。
     *
     * @param stockQuantity 検索する在庫数の上限
     * @return 在庫数が指定値以下の商品一覧
     */
    List<Product> findByStockQuantityLessThanEqual(Integer stockQuantity);
}
