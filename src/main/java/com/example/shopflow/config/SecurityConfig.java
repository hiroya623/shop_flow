
package com.example.shopflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ShopFlowのセキュリティ設定を管理するクラス。
 *
 * ログイン・ログアウトの動作、アクセス可能なURL、
 * パスワードのハッシュ化方式などを設定する。
 */
@Configuration
public class SecurityConfig {

    /**
     * パスワードをハッシュ化するためのオブジェクトを登録する。
     *
     * BCryptを使用することで、会員のパスワードを
     * 平文ではなくハッシュ値として保存できる。
     *
     * UserServiceなどでPasswordEncoderを利用できるようにする。
     *
     * @return BCrypt方式のPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Webアプリケーションのアクセス制御を設定する。
     *
     * 現段階では、会員登録ページとログインページを
     * ログイン前でも利用できるようにする。
     *
     * その他のページはログイン後に利用できる設定とする。
     *
     * @param http HTTPセキュリティ設定用オブジェクト
     * @return 設定済みのセキュリティフィルターチェーン
     * @throws Exception セキュリティ設定中にエラーが発生した場合
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        // URLごとのアクセス権限を設定する。
        http.authorizeHttpRequests(authorize -> authorize

                // 会員登録ページとログインページは誰でもアクセス可能。
                .requestMatchers("/register", "/login").permitAll()

                // CSS・JavaScript・画像などの静的ファイルも公開する。
                .requestMatchers("/css/**", "/js/**", "/images/**")
                .permitAll()

                // 上記以外のページはログイン済みのユーザーに限定する。
                .anyRequest().authenticated()
        );

        // ログイン画面とログイン成功後の動作を設定する。
        http.formLogin(form -> form
                // ログイン画面のURLを指定する。
                .loginPage("/login")

                // ログイン成功後は必ずダッシュボードへ移動する。
                // trueを指定することで、ログイン前にアクセスしようとしたURLではなく
                // /dashboardを優先する。
                .defaultSuccessUrl("/dashboard", true)

                // ログイン画面は未ログインでもアクセス可能にする。
                .permitAll()
        );


        // ログアウト後はログイン画面へ移動する。
        http.logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        );

        // 設定したセキュリティフィルターを返す。
        return http.build();
    }
}
