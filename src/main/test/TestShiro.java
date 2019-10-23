import com.wlc.po.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

/**
 * describe:
 *
 * @author 王立朝
 * @date 2019/10/23
 */
public class TestShiro {

    public Subject getSubject(User user){
        //获取工厂
        Factory<SecurityManager> factory= new IniSecurityManagerFactory("classpath:shiro.ini");
        //获取安全管理者实例
        SecurityManager securityManager = factory.getInstance();
        //把安全管理者实例放入到全局对象
        SecurityUtils.setSecurityManager(securityManager);
        //获取subject
        Subject subject = SecurityUtils.getSubject();
        return subject;
    }
    /**
     * 登录的方法
     **/
    public boolean login(User user) {
        //获取subject 当前对象
        Subject subject = getSubject(user);
        //如果用户已经登录过了，就退出，可以做 一个账户只能一个用户登录，多个用户登录，就会把另外一个
        //用户顶下去
        if(subject.isAuthenticated()){
            subject.logout();
        }
        UsernamePasswordToken token = new UsernamePasswordToken(user.getName(),user.getPassword());
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            System.out.println("认证失败！");
            e.printStackTrace();
            return false;
        }
        return subject.isAuthenticated();
    }

    //查看用户的角色
     public boolean hasRole(User user,String role){
        Subject subject = getSubject(user);
        return subject.hasRole(role);
     }

    public static void main(String[] args) {
        TestShiro testShiro = new TestShiro();
        //创建用户
        User user = new User();
        user.setName("zhangsan2");
        user.setPassword("1");

        //创建角色
        String role = "orderManager";

        //登录验证
        if (testShiro.login(user)) {
            System.out.println("登录成功！");
          if(testShiro.hasRole(user,role)){
              System.out.println("用户：" + user.getName() + " 拥有： " + role + "角色！");
          }else{
              System.out.println("用户：" + user.getName() + "没有拥有：" + role + "角色！");
          }
        }else{
            System.out.println("登录失败！");
        }

    }
}
