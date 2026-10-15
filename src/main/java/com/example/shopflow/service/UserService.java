
package com.example.shopflow.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopflow.entity.User;
import com.example.shopflow.repository.UserRepository;

/**
 * 会員登録など、会員に関する業務処理を担当するServiceクラス。
 *
 * Controllerから呼び出され、入力された会員情報の検証、
 * パスワードのハッシュ化、データベースへの保存などを行う。
 *
 * データベース操作そのものはUserRepositoryに任せる。
 */
@Service
public class UserService {

    /**
     * 会員情報を検索・保存するためのRepository。
     */
    private final UserRepository userRepository;

    /**
     * パスワードを安全なハッシュ値に変換するためのオブジェクト。
     *
     * Spring Securityの設定でPasswordEncoderをBeanとして
     * 登録することで、このクラスに自動注入される。
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * UserServiceのコンストラクタ。
     *
     * SpringがUserRepositoryとPasswordEncoderを自動注入する。
     *
     * @param userRepository 会員情報を扱うRepository
     * @param passwordEncoder パスワードをハッシュ化する処理
     */
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 新しい会員を登録する。
     *
     * ユーザー名とメールアドレスの重複を確認し、
     * パスワードをハッシュ化してからデータベースに保存する。
     *
     * 登録時の権限は一般会員（CUSTOMER）に固定する。
     * 管理者権限を会員登録画面から指定できないようにする。
     *
     * @param username 登録するユーザー名
     * @param email 登録するメールアドレス
     * @param rawPassword 入力されたハッシュ化前のパスワード
     * @return データベースに保存された会員情報
     * @throws IllegalArgumentException 入力値が不正な場合や、
     *         ユーザー名・メールアドレスが登録済みの場合
     */
    @Transactional
    public User registerUser(
            String username,
            String email,
            String rawPassword) {

        // ユーザー名が未入力の場合は登録を中止する。
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "ユーザー名を入力してください。");
        }

        // メールアドレスが未入力の場合は登録を中止する。
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "メールアドレスを入力してください。");
        }

        // パスワードが未入力の場合は登録を中止する。
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "パスワードを入力してください。");
        }

        // ユーザー名の長さを確認する。
        if (username.length() > 50) {
            throw new IllegalArgumentException(
                    "ユーザー名は50文字以内で入力してください。");
        }

        // メールアドレスの長さを確認する。
        if (email.length() > 255) {
            throw new IllegalArgumentException(
                    "メールアドレスは255文字以内で入力してください。");
        }

        // 同じユーザー名がすでに登録されていないか確認する。
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "このユーザー名はすでに使用されています。");
        }

        // 同じメールアドレスがすでに登録されていないか確認する。
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "このメールアドレスはすでに登録されています。");
        }

        // 新しい会員情報を保持するUserオブジェクトを作成する。
        User user = new User();

        // 入力されたユーザー名を設定する。
        user.setUsername(username);

        // 入力されたメールアドレスを設定する。
        user.setEmail(email);

        // パスワードをハッシュ化してから設定する。
        // 元のパスワードをそのままデータベースに保存しない。
        user.setPassword(passwordEncoder.encode(rawPassword));

        // 一般会員として登録する。
        // 管理者権限は会員登録処理から付与しない。
        user.setRole("CUSTOMER");

        // 登録直後のアカウントを有効にする。
        user.setEnabled(true);

        // 会員情報をデータベースに保存し、保存結果を返す。
        return userRepository.save(user);
    }
}
