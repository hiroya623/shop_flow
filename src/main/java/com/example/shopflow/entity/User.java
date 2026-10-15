
package com.example.shopflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * ShopFlowの会員情報を管理するエンティティクラス。
 *
 * このクラスの各フィールドは、MySQLのusersテーブルの
 * 各カラムに対応する。
 *
 * 会員登録、ログイン、会員情報の検索などで使用する。
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        // 同じユーザー名の会員が重複登録されることを防ぐ。
        @UniqueConstraint(columnNames = "username"),

        // 同じメールアドレスの会員が重複登録されることを防ぐ。
        @UniqueConstraint(columnNames = "email")
    }
)
public class User {

    /**
     * 会員を一意に識別するID。
     * データベースへの登録時に自動採番される。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ログイン時などに使用するユーザー名。
     * 必須項目で、最大50文字まで格納できる。
     */
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * 会員のメールアドレス。
     * 必須項目で、最大255文字まで格納できる。
     */
    @Column(nullable = false, length = 255)
    private String email;

    /**
     * 会員のパスワード。
     * 平文ではなく、パスワードハッシュを保存する。
     * 必須項目で、最大255文字まで格納できる。
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 会員の権限を表す文字列。
     * CUSTOMERは一般会員、ADMINは管理者を表す。
     * 新しいインスタンスではCUSTOMERを初期値とする。
     */
    @Column(nullable = false, length = 20)
    private String role = "CUSTOMER";

    /**
     * アカウントが有効かどうかを表すフラグ。
     * trueは有効、falseは無効を表す。
     * 新しいインスタンスでは有効に設定する。
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * JPAが会員情報のインスタンスを生成する際に使用する
     * 引数なしのコンストラクタ。
     */
    public User() {
    }

    /**
     * 会員IDを取得する。
     *
     * @return 会員ID。登録前はnullの場合がある。
     */
    public Long getId() {
        return id;
    }

    /**
     * ユーザー名を取得する。
     *
     * @return ユーザー名
     */
    public String getUsername() {
        return username;
    }

    /**
     * ユーザー名を設定する。
     *
     * @param username 設定するユーザー名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * メールアドレスを取得する。
     *
     * @return メールアドレス
     */
    public String getEmail() {
        return email;
    }

    /**
     * メールアドレスを設定する。
     *
     * @param email 設定するメールアドレス
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 保存されているパスワードハッシュを取得する。
     *
     * @return パスワードハッシュ
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードハッシュを設定する。
     *
     * @param password 設定するパスワードハッシュ
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 会員の権限を取得する。
     *
     * @return CUSTOMERまたはADMINなどの権限文字列
     */
    public String getRole() {
        return role;
    }

    /**
     * 会員の権限を設定する。
     *
     * @param role 設定する権限文字列
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * アカウントが有効かどうかを取得する。
     *
     * @return 有効ならtrue、無効ならfalse
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * アカウントの有効・無効を設定する。
     *
     * @param enabled trueなら有効、falseなら無効
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
