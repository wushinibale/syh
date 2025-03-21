package com.sky.context;

/**
 * @author 上玄
 * @version 1.0
 * @date 2024/09/20 20:11
 */

/**
 * 基础上下文获取当前线程用户ID
 * @author sky
 */
public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
