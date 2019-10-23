package com.wlc.dao;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * describe:查询数据库的类
 *
 * @author 王立朝
 * @date 2019/10/23
 */
public class DAO {

    public DAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     **/
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/shiro02?characterEncoding=UTF-8",
                "scott", "tiger");

    }

    /**
     * 根据用户名 获取用户名密码
     **/
    public String getPassword(String userName) {
        try {
            Connection connection = getConnection();
            String sql = "select password from user where name =?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据用户名获取用户的角色
     **/
    public Set<String> listRole(String userName) {
        Set<String> roleSet = new HashSet<>();
        String sql = "select r.name from user u left join user_role ur on ur.uid = u.id left join role r on ur.rid = r.id where u.name = ?";
        roleSet = commonSqlPre(sql, userName, roleSet);
        return roleSet;
    }

    /**
     * 公共部分的查询，抽出到一个方法中，提高重用性
     **/
    public Set<String> commonSqlPre(String sql, String userName, Set<String> permissionSet) {
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                permissionSet.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissionSet;
    }

    /**
     * 根据用户名，获取用户所具有的权限
     **/
    public Set<String> listPermissions(String userName) {
        Set<String> permissionSet = new HashSet<>();
        String sql = "SELECT p.name FROM USER u LEFT JOIN user_role ur ON ur.uid = u.id left join role r on ur.rid = r.id LEFT JOIN role_permission rp ON rp.rid = ur.rid LEFT JOIN permission p ON rp.pid = p.id where  u.name=?";
        permissionSet = commonSqlPre(sql, userName, permissionSet);
        return permissionSet;
    }


    public static void main(String[] args) {
        DAO dao = new DAO();
        System.out.println(dao.listPermissions("zhang3"));

    }

}
