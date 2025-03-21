package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Now;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("password: " + password);
        //密码比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 员工注册
     *
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //密码md5加密
        String password = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
        employee.setPassword(password);

        //状态默认设置为启用
        employee.setStatus(StatusConstant.ENABLE);

        //时间赋值
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //创建者和修改者

        //根据登录用户信息获取用户id
        Long empId = BaseContext.getCurrentId();

//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     * @return PageResult
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //1、分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        //2、封装分页结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(records);


        //2、处理查询结果
        return pageResult;
    }

    public void updateEmployeeStatus(Integer status, Long id) {
        // 构建员工对象
//        Employee employee = new Employee();
//        employee.setId(id);
//        employee.setStatus(status);
        //建造者模式
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();
        //打印日志
        log.info("employee: " + employee);
        //更新员工状态
        employeeMapper.updateEmployee(employee);
    }

    /**
     * 根据id查询员工详情
     * @param id
     * @return
     */
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        //判断是否为空
        if (employee != null) {
            //密码隐藏
            employee.setPassword("******");
            return employee;
        }
        return null;
    }

    /**
     * 更新员工信息
     * @param employeeDTO
     */
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        // 更新时间
//        employee.setUpdateTime(LocalDateTime.now());
        // 根据当前线程登录用户信息获取用户id
//        Long empId = BaseContext.getCurrentId();
//         设置修改人
//        employee.setUpdateUser(empId);
        // 更新员工信息
        employeeMapper.updateEmployee(employee);
    }
}
