
package com.example.shop_flow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * ShopFlowで取り扱う商品情報を管理するエンティティ。
 *
 * 商品名、説明、価格、在庫数を保持する。
 * JPAによってデータベースのproductsテーブルと対応付けられる。
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * 商品を一意に識別するID。
     * データベースへの登録時に自動採番される。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品名。
     * 未入力を禁止し、最大100文字まで登録できる。
     */
    @NotBlank(message = "商品名を入力してください。")
    @Size(max = 100, message = "商品名は100文字以内で入力してください。")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 商品の説明。
     * 未入力も許可し、最大1000文字まで登録できる。
     */
    @Size(max = 1000, message = "商品説明は1000文字以内で入力してください。")
    @Column(length = 1000)
    private String description;

    /**
     * 商品の販売価格。
     * 金額の小数誤差を避けるため、価格は整数で管理する。
     */
    @NotNull(message = "価格を入力してください。")
    @Min(value = 0, message = "価格は0以上で入力してください。")
    @Column(nullable = false)
    private Integer price;

    /**
     * 商品の在庫数。
     * 初期値を0とし、マイナスの在庫数を禁止する。
     */
    @NotNull(message = "在庫数を入力してください。")
    @PositiveOrZero(message = "在庫数は0以上で入力してください。")
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    /**
     * JPAがエンティティを生成するために使用する引数なしコンストラクタ。
     */
    public Product() {
    }

    /**
     * 商品IDを取得する。
     *
     * @return データベースで採番された商品ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 商品IDを設定する。
     *
     * @param id 設定する商品ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 商品名を取得する。
     *
     * @return 商品名
     */
    public String getName() {
        return name;
    }

    /**
     * 商品名を設定する。
     *
     * @param name 設定する商品名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 商品説明を取得する。
     *
     * @return 商品説明
     */
    public String getDescription() {
        return description;
    }

    /**
     * 商品説明を設定する。
     *
     * @param description 設定する商品説明
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 商品価格を取得する。
     *
     * @return 商品価格（整数）
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * 商品価格を設定する。
     *
     * @param price 設定する商品価格
     */
    public void setPrice(Integer price) {
        this.price = price;
    }

    /**
     * 商品の在庫数を取得する。
     *
     * @return 現在の在庫数
     */
    public Integer getStockQuantity() {
        return stockQuantity;
    }

    /**
     * 商品の在庫数を設定する。
     *
     * @param stockQuantity 設定する在庫数
     */
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
