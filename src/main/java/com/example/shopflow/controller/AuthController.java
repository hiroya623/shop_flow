
package com.example.shopflow.controller;

import com.example.shopflow.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ShopFlowの会員登録画面とログイン画面を制御するクラス。
 *
 * GETリクエストでは各画面を表示し、
 * POSTリクエストでは会員登録フォームから送信された情報を処理する。
 */
@Controller
public class AuthController {

    /**
     * 会員情報の登録処理を担当するサービス。
     *
     * 入力値の検証、重複チェック、パスワードのハッシュ化、
     * データベースへの保存はUserServiceに任せる。
     */
    private final UserService userService;

    /**
     * コンストラクタ。
     *
     * SpringからUserServiceを受け取り、このクラスで利用する。
     *
     * @param userService 会員情報を登録するサービス
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 会員登録画面を表示する。
     *
     * @return 会員登録画面のHTMLテンプレート名
     */
    @GetMapping("/register")
    public String showRegisterForm() {
        // templates/register.htmlを表示する。
        return "register";
    }

    /**
     * 会員登録フォームから送信された情報を処理する。
     *
     * ユーザー名、メールアドレス、パスワードを受け取り、
     * UserServiceを通じて会員情報をデータベースに登録する。
     *
     * 登録に成功した場合はログイン画面へ移動する。
     * 入力内容に問題がある場合は会員登録画面へ戻る。
     *
     * @param username フォームに入力されたユーザー名
     * @param email フォームに入力されたメールアドレス
     * @param password フォームに入力された未ハッシュ化のパスワード
     * @return 登録結果に応じたリダイレクト先
     */
    @PostMapping("/register")
    public String registerUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        try {
            // 入力値の検証、重複チェック、パスワードのハッシュ化、
            // データベースへの保存をサービスに依頼する。
            userService.registerUser(username, email, password);

            // 登録完了後はログイン画面へ移動する。
            // URLのregisteredパラメーターを使って完了メッセージを表示する。
            return "redirect:/login?registered";

        } catch (IllegalArgumentException e) {
            // 入力値の不備や重複登録があった場合は登録画面へ戻る。
            // エラーの詳細は画面に直接出さず、画面側で案内する。
            return "redirect:/register?error";
        }
    }

    /**
     * ログイン画面を表示する。
     *
     * ログイン認証そのものはSpring Securityが処理する。
     *
     * @return ログイン画面のHTMLテンプレート名
     */
    @GetMapping("/login")
    public String showLoginForm() {
        // templates/login.htmlを表示する。
        return "login";
    }
}
