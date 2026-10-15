
package com.example.shop_flow.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shop_flow.entity.Product;
import com.example.shop_flow.service.ProductService;

/**
 * 一般ユーザー向けの商品閲覧画面を制御するクラス。
 *
 * 管理者向けのProductControllerとは役割を分離し、
 * 商品一覧と商品詳細の表示だけを担当する。
 */
@Controller
@RequestMapping("/products")
public class ProductViewController {

    /**
     * 商品情報の取得を担当するサービス。
     * 商品一覧や商品詳細のデータ取得に使用する。
     */
    private final ProductService productService;

    /**
     * 商品サービスを受け取り、このコントローラーで利用できるようにする。
     *
     * @param productService 商品情報を取得するサービス
     */
    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 商品一覧画面を表示する。
     *
     * GET /products へのアクセス時に、
     * データベースから商品一覧を取得して画面に渡す。
     *
     * @param model 画面へ渡すデータを格納するオブジェクト
     * @return 表示するThymeleafテンプレート名
     */
    @GetMapping
    public String listProducts(Model model) {
        // 登録されている商品をすべて取得する。
        List<Product> products = productService.findAll();

        // 取得した商品一覧をテンプレートで使用できるようにする。
        model.addAttribute("products", products);

        // templates/products.html を表示する。
        return "products";
    }

    /**
     * 商品詳細画面を表示する。
     *
     * GET /products/{id} へのアクセス時に、
     * URLで指定された商品IDの商品情報を取得する。
     *
     * @param id 表示する商品のID
     * @param model 画面へ渡すデータを格納するオブジェクト
     * @return 商品詳細テンプレート名、または商品一覧へのリダイレクト
     */
    @GetMapping("/{id}")
    public String showProductDetail(
            @PathVariable Long id,
            Model model) {

        try {
            // 指定されたIDの商品を取得する。
            Product product = productService.findById(id);

            // 取得した商品情報を詳細画面へ渡す。
            model.addAttribute("product", product);

            // templates/product-detail.html を表示する。
            return "product-detail";

        } catch (IllegalArgumentException e) {
            // 存在しない商品IDなどの場合は一覧画面へ戻す。
            return "redirect:/products";
        }
    }
}
