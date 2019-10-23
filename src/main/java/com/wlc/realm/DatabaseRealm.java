package com.wlc.realm;

import com.wlc.dao.DAO;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Set;

/**
 * describe:
 * 继承 AuthorizingRealm 类，登录最后的验证时在realm中验证的
 * DatabaseRealm 就是用来通过数据库 验证用户，和相关授权的类。
 * 两个方法分别做验证和授权：
 * doGetAuthenticationInfo(), 验证
 *
 * doGetAuthorizationInfo()  授权
 *
 * @author 王立朝
 * @date 2019/10/23
 */
public class DatabaseRealm extends AuthorizingRealm {
    /**
     * 授权
     * **/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //这个部分是进行授权的代码，如果可以进入到这里，就说明 登录已经通过了
        //获取用户名
        String userName = (String) principalCollection.getPrimaryPrincipal();
        //根据用户名获取用户的角色
        Set<String> roleSet = new DAO().listRole(userName);
        //根据用户名获取用户拥有的角色的权限信息
        Set<String> permissionsSet = new DAO().listPermissions(userName);

        //授权对象
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //把通过 dao 获取到的 角色和权限放入到 授权对象中
        simpleAuthorizationInfo.setRoles(roleSet);
        simpleAuthorizationInfo.setStringPermissions(permissionsSet);
        return simpleAuthorizationInfo;
    }

    /**验证**/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取用户名和密码
       /* String password = authenticationToken.getCredentials().toString();*/
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String password = new String(token.getPassword());
        String userName = authenticationToken.getPrincipal().toString();
        //读取数据库中的用户名和密码
        String passWordInnDb = new DAO().getPassword(userName);
        //数据库中的密码为空或者和密码不一样，就报错
        if(null == passWordInnDb || !passWordInnDb.equals(password)){
            throw new AuthenticationException();
        }
        //最后把用户的 用户名和密码放到 认证信息里面
        SimpleAuthenticationInfo  simpleAuthenticationInfo = new SimpleAuthenticationInfo(userName,password,getName());
        return simpleAuthenticationInfo;
    }
}
