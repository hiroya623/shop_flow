
package com.example.shop_flow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ShopFlowの管理者向け画面を制御するクラス。
 *
 * 管理者向けURLへのアクセス権限は、SecurityConfigクラスで制御する。
 * このクラスでは、管理者向け画面の表示処理を担当する。
 */
@Controller
public class AdminController {

    /**
     * 管理者用ダッシュボードを表示する。
     *
     * GETリクエストで「/admin/dashboard」にアクセスした際に実行される。
     * このURLはSpring SecurityによってADMIN権限を持つユーザーに限定される。
     *
     * @return 管理者用ダッシュボードのテンプレート名
     */
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard() {

        // templates/admin-dashboard.htmlを表示する。
        return "admin-dashboard";
    }
}
