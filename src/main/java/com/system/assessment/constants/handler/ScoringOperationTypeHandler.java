package com.system.assessment.constants.handler;

import com.system.assessment.constants.Role;
import com.system.assessment.constants.ScoringOperationType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScoringOperationTypeHandler implements TypeHandler<String> {
    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {

        int code = ScoringOperationType.getCodeByDescription(parameter);
        if (code != -1) {
            ps.setInt(i, code);
        } else {
            ps.setNull(i, java.sql.Types.INTEGER); // 当没有匹配的角色时设置为 null 或其他默认值
        }

    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        Integer code = (Integer) rs.getObject(columnName);  // 获取Integer类型的值
        if (code == null) {
            return null; // 或其他适合的表示null的值，如"None"或"未知"
        }
        return ScoringOperationType.getDescriptionByCode(code);
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer code = (Integer) rs.getObject(columnIndex);  // 获取Integer类型的值
        if (code == null) {
            return null; // 或其他适合的表示null的值，如"None"或"未知"
        }
        return ScoringOperationType.getDescriptionByCode(code);
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer code = (Integer) cs.getObject(columnIndex);  // 获取Integer类型的值
        if (code == null) {
            return null; // 或其他适合的表示null的值，如"None"或"未知"
        }
        return ScoringOperationType.getDescriptionByCode(code);
    }
}
