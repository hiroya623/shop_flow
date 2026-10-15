
package com.example.shop_flow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ShopFlowのログイン後に表示するダッシュボードを制御するクラス。
 *
 * 現段階では、ログインに成功したユーザーに
 * ダッシュボード画面を表示する役割を担当する。
 */
@Controller
public class DashboardController {

    /**
     * ダッシュボード画面を表示する。
     *
     * このURLはSecurityConfigの設定により、
     * ログイン済みのユーザーだけがアクセスできる。
     *
     * @return ダッシュボード画面のHTMLテンプレート名
     */
    @GetMapping("/dashboard")
    public String showDashboard() {
        // templates/dashboard.htmlを表示する。
        return "dashboard";
    }
}
