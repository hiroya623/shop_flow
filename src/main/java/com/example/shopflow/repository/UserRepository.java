
package com.example.shopflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopflow.entity.User;

/**
 * 会員情報をデータベースから検索・保存するためのRepositoryクラス。
 *
 * JpaRepositoryを継承することで、会員情報の登録・更新・削除・
 * 全件取得などの基本的なデータベース操作を利用できる。
 *
 * また、Spring Data JPAの命名規則に従ってメソッドを定義することで、
 * SQLを直接記述せずに会員情報を検索できる。
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ユーザー名を指定して会員情報を検索する。
     *
     * ログイン時に、入力されたユーザー名に対応する会員を
     * 取得する場合などに使用する。
     *
     * @param username 検索対象のユーザー名
     * @return 該当する会員が存在する場合はUser、
     *         存在しない場合は空のOptional
     */
    Optional<User> findByUsername(String username);

    /**
     * メールアドレスを指定して会員情報を検索する。
     *
     * メールアドレスが登録済みか確認したり、
     * メールアドレスから会員を特定したりする場合に使用する。
     *
     * @param email 検索対象のメールアドレス
     * @return 該当する会員が存在する場合はUser、
     *         存在しない場合は空のOptional
     */
    Optional<User> findByEmail(String email);

    /**
     * 指定されたユーザー名がすでに登録されているか確認する。
     *
     * 会員登録時に、同じユーザー名の重複登録を防ぐために使用する。
     *
     * @param username 登録済みか確認するユーザー名
     * @return 登録済みの場合はtrue、未登録の場合はfalse
     */
    boolean existsByUsername(String username);

    /**
     * 指定されたメールアドレスがすでに登録されているか確認する。
     *
     * 会員登録時に、同じメールアドレスの重複登録を防ぐために使用する。
     *
     * @param email 登録済みか確認するメールアドレス
     * @return 登録済みの場合はtrue、未登録の場合はfalse
     */
    boolean existsByEmail(String email);
}
