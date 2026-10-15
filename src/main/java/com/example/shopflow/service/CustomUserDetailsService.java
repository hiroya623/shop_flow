
package com.example.shopflow.service;

import com.example.shopflow.entity.User;
import com.example.shopflow.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ShopFlowのログイン認証で使用するユーザー情報取得クラス。
 *
 * Spring Securityから呼び出され、MySQLのusersテーブルを検索して
 * ログイン対象のユーザー情報を取得する。
 *
 * 取得した情報をSpring Securityが扱えるUserDetails形式に変換する。
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * データベースからユーザー情報を取得するためのリポジトリ。
     *
     * ユーザー名を条件として、登録済みの会員情報を検索する。
     */
    private final UserRepository userRepository;

    /**
     * コンストラクタ。
     *
     * SpringからUserRepositoryを受け取り、
     * このクラスで利用できるように設定する。
     *
     * @param userRepository ユーザー情報を検索するリポジトリ
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ユーザー名を使ってログイン対象のユーザーを取得する。
     *
     * Spring Securityがログイン認証を行う際に、このメソッドを呼び出す。
     *
     * データベースからユーザーを検索し、ユーザー名・ハッシュ化済み
     * パスワード・権限・有効状態をSpring Securityに渡す。
     *
     * @param username ログイン画面で入力されたユーザー名
     * @return Spring Securityが認証に使用するユーザー情報
     * @throws UsernameNotFoundException 指定されたユーザーが存在しない場合
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // ユーザー名を条件として、データベースから会員情報を検索する。
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "ユーザーが見つかりません。"));

        // データベースの会員情報をSpring Security用の形式に変換する。
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())

                // 登録時にBCryptでハッシュ化したパスワードを設定する。
                // 入力されたパスワード自体は、ここでは保存・設定しない。
                .password(user.getPassword())

                // データベースの権限をSpring Securityの権限に変換する。
                // 例：CUSTOMERはROLE_CUSTOMERとして扱われる。
                .roles(user.getRole())

                // 無効化されたアカウントはログインできないようにする。
                .disabled(!user.isEnabled())

                // 設定した情報からUserDetailsを作成して返す。
                .build();
    }
}
