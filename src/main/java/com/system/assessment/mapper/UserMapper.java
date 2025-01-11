package com.system.assessment.mapper;

import com.system.assessment.pojo.User;
import com.system.assessment.vo.UserInfoSelectVO;
import com.system.assessment.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Mapper
public interface UserMapper {

    public Integer setWeight1(@Param("id")Integer id, @Param("weight")Double weight);

    public Integer setWeight2(@Param("id")Integer id, @Param("weight")Double weight);

    public Integer setWeight3(@Param("id")Integer id, @Param("weight")Double weight);

    public Integer setSuperVisor1(@Param("id")Integer id,
                                  @Param("superVisor")Integer superVisor);

    public Integer setSuperVisor2(@Param("id")Integer id,
                                  @Param("superVisor")Integer superVisor);

    public Integer setSuperVisor3(@Param("id")Integer id,
                                  @Param("superVisor")Integer superVisor);

    public Integer setSuperWeight1(@Param("id")Integer id,
                                  @Param("weight")Double weight);

    public Integer setSuperWeight2(@Param("id")Integer id,
                                   @Param("weight")Double weight);

    public Integer setSuperWeight3(@Param("id")Integer id,
                                   @Param("weight")Double weight);

    public Integer updateWeightsAndSuperVisor(@Param("id")Integer id);

    @Select("select id from user where name = #{name} and work_num = #{workNum} and is_delete = 0")
    public Integer findIdByNameAndWorkNum(@Param("name") String name,
                                              @Param("workNum")String workNum);

    @Select("select hr from user where id = #{id}")
    public Integer findHrById(@Param("id")Integer id);

    @Select("select email from user where id = #{id}")
    public String findEmailById(@Param("id")Integer id);

    public Integer setWeight(@Param("weight")Double weight);

    public Integer deleteClean();

    public Integer updateFirstLogin(@Param("id")Integer id);

    @Select("select role from user where id = #{id}")
    public Integer findRole(@Param("id")Integer id);

    public Integer updateSupervisorAndAdmin(@Param("id")Integer id);

    public Integer updateHRBP(@Param("id")Integer id);

    @Update("update  user set supervisor_1 = #{supervisor} where id = #{id}")
    public Integer updateSupervisor1ById(@Param("id") Integer id,
                                         @Param("supervisor")Integer supervisor);

    @Update("update  user set supervisor_2 = #{supervisor} where id = #{id}")
    public Integer updateSupervisor2ById(@Param("id") Integer id,
                                         @Param("supervisor")Integer supervisor);

    @Update("update  user set supervisor_3 = #{supervisor} where id = #{id}")
    public Integer updateSupervisor3ById(@Param("id") Integer id,
                                         @Param("supervisor")Integer supervisor);


    @Update("update  user set first_admin = #{firstAdmin} where id = #{id}")
    public Integer updateFirstAdminById(@Param("id") Integer id,
                                         @Param("firstAdmin")Integer firstAdmin);

    @Update("update  user set second_admin = #{secondAdmin} where id = #{id}")
    public Integer updateSecondAdminById(@Param("id") Integer id,
                                        @Param("secondAdmin")Integer secondAdmin);

    @Update("update  user set super_admin = #{superAdmin} where id = #{id}")
    public Integer updateSuperAdminById(@Param("id") Integer id,
                                        @Param("superAdmin")Integer superAdmin);


    @Update("update  user set hr = #{hr} where id = #{id}")
    public Integer updateHrById(@Param("id") Integer id,
                                         @Param("hr")Integer hr);

    @Select("select distinct department from user")
    public List<String> findDepartment();

    @Select("select id from user where name = #{name}")
    public Integer findIdByName(@Param("name")String name);

    @Select("select id from user where name = #{name} and is_delete = 0")
    public Integer findIdByNameAndIsDelete(@Param("name")String name);

    @Select("select * from user where name = #{name}")
    public User findUserByName(@Param("name") String name);

    @Select("select * from user where name = #{name} and is_delete = 0")
    public User findValuedUserByName(@Param("name") String name);

    @Select("select * from user where name = #{name} and work_num = #{workNum} and is_delete = 0")
    public User findValuedUserByNameAndWorkNum(@Param("name") String name,
                                               @Param("workNum")String workNum);


    @Select("select * from user where name = #{name} and is_delete = 0")
    public User findValuedUserByHrName(@Param("name") String name);

    public Integer deleteUser(@Param("id") Integer userId);

    public List<UserVO> findBasicInfos(@Param("selectVo") UserInfoSelectVO userInfoSelectVO);

    public User findRoleById(@Param("id")Integer userId);

    @Select("select name from user where id = #{id}")
    public User findNameById(@Param("id") Integer id);

    @Select("select name from user where id = #{id}")
    public String findNameByUserId(@Param("id") Integer id);

    @Select("select work_num from user where id = #{id}")
    public String findWorkNumById(@Param("id") Integer id);

    @Select("select direct_supervisor from user where id = #{id}")
    public Integer findDirectSupervisorById(@Param("id") Integer id);

    public Integer updateUserInfo(@Param("user") UserVO user);

    public Integer updateUserInfoInit(@Param("user") UserVO user);

    public User findBasicInfoBySelfId(@Param("userId") Integer userId);

    public Integer updatePassword(@Param("newPassword")String newPassword, @Param("userId") Integer userId);

    public Integer addUsersByExcel(@Param("userList") List<User> users);

    public Integer addUser(@Param("user") User user);

    public Integer addUserVO(@Param("user") UserVO user);

    public String findPassword(@Param("userId")Integer userId);
}
