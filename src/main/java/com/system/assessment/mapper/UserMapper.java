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
    public Integer setWeight(@Param("weight")Double weight);

    public Integer deleteClean();

    public Integer updateFirstLogin(@Param("id")Integer id);

    @Select("select role from user where id = #{id}")
    public Integer findRole(@Param("id")Integer id);

    public Integer updateSupervisor(@Param("id")Integer id);

    @Update("update  user set supervisor_1 = #{supervisor} where id = #{id}")
    public Integer updateSupervisor1ById(@Param("id") Integer id,
                                         @Param("supervisor")Integer supervisor);

    @Update("update  user set supervisor_2 = #{supervisor} where id = #{id}")
    public Integer updateSupervisor2ById(@Param("id") Integer id,
                                         @Param("supervisor")Integer supervisor);

    @Update("update  user set supervisor_3 = #{supervisor} where id = #{id}")
    public Integer updateSupervisor3ById(@Param("id") Integer id,
                                         @Param("supervisor")Integer supervisor);

    @Update("update  user set supervisor_4 = #{supervisor} where id = #{id}")
    public Integer updateSupervisor4ById(@Param("id") Integer id,
                                         @Param("supervisor")Integer supervisor);

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

    @Select("select * from user where name = #{name} and is_delete = 0")
    public User findValuedUserByHrName(@Param("name") String name);

    public Integer deleteUser(@Param("id") Integer userId);

    public List<UserVO> findBasicInfos(@Param("selectVo") UserInfoSelectVO userInfoSelectVO);

    public User findRoleById(@Param("id")Integer userId);

    @Select("select name from user where id = #{id}")
    public User findNameById(@Param("id") Integer id);

    @Select("select name from user where id = #{id}")
    public String findNameByUserId(@Param("id") Integer id);

    @Select("select direct_supervisor from user where id = #{id}")
    public Integer findDirectSupervisorById(@Param("id") Integer id);

    public Integer updateUserInfo(@Param("user") UserVO user);

    public Integer updateUserInfoInit(@Param("user") UserVO user);

    public User findBasicInfoBySelfId(@Param("userId") Integer userId);

    public Integer updatePassword(@Param("newPassword")String newPassword, @Param("userId") Integer userId);

    public Integer addUsersByExcel(@Param("userList") List<User> users);

    public Integer addUser(@Param("user") User user);

    public String findPassword(@Param("userId")Integer userId);
}
