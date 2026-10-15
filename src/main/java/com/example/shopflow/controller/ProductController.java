
package com.example.shopflow.controller;

import com.example.shopflow.entity.Product;
import com.example.shopflow.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ShopFlowの商品管理画面を制御するコントローラー。
 *
 * 商品一覧、商品登録、商品編集、商品削除、在庫検索などの
 * HTTPリクエストを受け取り、ProductServiceへ処理を依頼する。
 *
 * URLを/admin/products配下に統一することで、
 * SecurityConfigによる管理者権限の制限を適用する。
 */
@Controller
@RequestMapping("/admin/products")
public class ProductController {

    /** 商品の登録・検索・更新・削除を担当するサービス。 */
    private final ProductService productService;

    /**
     * 商品サービスを受け取り、コントローラーを初期化する。
     *
     * @param productService 商品管理の業務処理を行うサービス
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 商品一覧画面を表示する。
     *
     * URL: GET /admin/products
     *
     * @param model 画面に渡す商品一覧などのデータ
     * @return 商品一覧画面のテンプレート名
     */
    @GetMapping
    public String listProducts(Model model) {
        // データベースからすべての商品を取得して画面へ渡す。
        model.addAttribute("products", productService.findAll());

        // templates/admin/products.htmlを表示する。
        return "admin/products";
    }

    /**
     * 商品登録画面を表示する。
     *
     * URL: GET /admin/products/new
     *
     * @param model 画面に渡す新規商品オブジェクト
     * @return 商品登録画面のテンプレート名
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // フォームと対応付ける空の商品オブジェクトを用意する。
        model.addAttribute("product", new Product());

        // templates/admin/product-form.htmlを表示する。
        return "admin/product-form";
    }

    /**
     * 商品登録フォームの入力内容を検証して登録する。
     *
     * URL: POST /admin/products
     *
     * @param product フォームから送信された商品情報
     * @param bindingResult 入力検証の結果
     * @param redirectAttributes リダイレクト先へ渡すメッセージ
     * @return 登録成功時は一覧画面、入力エラー時は登録画面
     */
    @PostMapping
    public String createProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // 入力検証でエラーがあれば登録せず、フォームへ戻す。
        if (bindingResult.hasErrors()) {
            return "admin/product-form";
        }

        try {
            // 商品登録処理をサービスへ依頼する。
            productService.create(product);

            // 登録完了メッセージをリダイレクト先へ渡す。
            redirectAttributes.addFlashAttribute(
                    "successMessage", "商品を登録しました。");

            // 商品一覧画面へ移動する。
            return "redirect:/admin/products";

        } catch (IllegalArgumentException exception) {
            // サービス側の入力チェックエラーをフォームに表示する。
            bindingResult.reject("product.error", exception.getMessage());
            return "admin/product-form";
        }
    }

    /**
     * 商品編集画面を表示する。
     *
     * URL: GET /admin/products/{id}/edit
     *
     * @param id 編集対象の商品ID
     * @param model 画面に渡す商品情報
     * @return 商品編集画面のテンプレート名
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // 指定されたIDの商品を取得してフォームに設定する。
            model.addAttribute("product", productService.findById(id));
            return "admin/product-form";

        } catch (IllegalArgumentException exception) {
            // 存在しない商品IDの場合は一覧へ戻す。
            redirectAttributes.addFlashAttribute(
                    "errorMessage", exception.getMessage());
            return "redirect:/admin/products";
        }
    }

    /**
     * 商品編集フォームの入力内容を検証して更新する。
     *
     * URL: POST /admin/products/{id}/edit
     *
     * @param id 更新対象の商品ID
     * @param product フォームから送信された商品情報
     * @param bindingResult 入力検証の結果
     * @param redirectAttributes リダイレクト先へ渡すメッセージ
     * @return 更新成功時は一覧画面、入力エラー時は編集画面
     */
    @PostMapping("/{id}/edit")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // 入力エラーがある場合は、更新処理を実行しない。
        if (bindingResult.hasErrors()) {
            return "admin/product-form";
        }

        try {
            // 指定された商品を更新する。
            productService.update(id, product);

            // 更新完了メッセージを一覧画面へ渡す。
            redirectAttributes.addFlashAttribute(
                    "successMessage", "商品情報を更新しました。");

            return "redirect:/admin/products";

        } catch (IllegalArgumentException exception) {
            // 商品が存在しない場合や値が不正な場合はエラーを表示する。
            bindingResult.reject("product.error", exception.getMessage());
            return "admin/product-form";
        }
    }

    /**
     * 商品の在庫数を検索する。
     *
     * URL: GET /admin/products/stock
     *
     * @param stockQuantity 検索する在庫数の上限
     * @param model 画面に渡す検索結果
     * @return 商品一覧画面のテンプレート名
     */
    @GetMapping("/stock")
    public String searchByStock(
            @RequestParam(defaultValue = "5") Integer stockQuantity,
            Model model) {

        // 指定した在庫数以下の商品を検索する。
        model.addAttribute(
                "products",
                productService.findByStockQuantityLessThanEqual(stockQuantity));

        // 検索条件も画面へ渡し、入力欄に表示できるようにする。
        model.addAttribute("stockQuantity", stockQuantity);

        return "admin/products";
    }

    /**
     * 商品を削除する。
     *
     * URL: POST /admin/products/{id}/delete
     *
     * @param id 削除対象の商品ID
     * @param redirectAttributes リダイレクト先へ渡すメッセージ
     * @return 商品一覧画面へのリダイレクト
     */
    @PostMapping("/{id}/delete")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            // 指定された商品を削除する。
            productService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "商品を削除しました。");

        } catch (IllegalArgumentException exception) {
            // 商品が存在しない場合はエラーメッセージを表示する。
            redirectAttributes.addFlashAttribute(
                    "errorMessage", exception.getMessage());
        }

        // 削除後は商品一覧画面へ戻す。
        return "redirect:/admin/products";
    }
}
