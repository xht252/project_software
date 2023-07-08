/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.gtask.exception;

//
public class NetworkFailureException extends Exception {
    private static final long serialVersionUID = 2107610287180234136L;

    // 这是一个默认构造函数，调用父类的默认构造函数
    public NetworkFailureException() {
        super();
    }

    // 该构造函数接受一个字符串参数，表示与异常关联的错误消息
    public NetworkFailureException(String paramString) {
        super(paramString);
    }

    // 该构造函数接受两个参数:表示错误消息的字符串和表示异常原因的Throwable对象
    public NetworkFailureException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }
}
