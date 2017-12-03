# practise

How to start the practise application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/dropwizard-practise-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

## how to setup a dropwizard project by maven

mvn archetype:generate -DarchetypeGroupId=io.dropwizard.archetypes -DarchetypeArtifactId=java-simple -DarchetypeVersion=1.0.0

## problems to record

when put findByName api into resource PeopleResource.class, error happens, e.g Parameter is not a number.

## Hibernate Reference Documentation 3.3.1解释如下：

Automatically validate or export schema DDL to the database when the SessionFactory is created.

With create-drop, the database schema will be dropped when the SessionFactory is closed explicitly.
eg. validate | update | create | create-drop



其实这个hibernate.hbm2ddl.auto参数的作用主要用于：自动创建|更新|验证数据库表结构。如果不是此方面的需求建议set value="none"。

create：
   每次加载hibernate时都会删除上一次的生成的表，然后根据你的model类再重新来生成新表，哪怕两次没有任何改变也要这样执行，这就是导致数据库表数据丢失的一个重要原因。

create-drop ：
    每次加载hibernate时根据model类生成表，但是sessionFactory一关闭,表就自动删除。

update：
    最常用的属性，第一次加载hibernate时根据model类会自动建立起表的结构（前提是先建立好数据库），以后加载hibernate时根据model类自动更新表结构，即使表结构改变了但表中的行仍然存在不会删除以前的行。要注意的是当部署到服务器后，表结构是不会被马上建立起来的，是要等应用第一次运行起来后才会。

validate ：
    每次加载hibernate时，验证创建数据库表结构，只会和数据库中的表进行比较，不会创建新表，但是会插入新值。


## About WebApplicationException

restful 开发过程中 如果要在返回的信息中包含 具体的错误信息，javax.ws.rs-api库里提供了WebApplicationException, 该类继承自RuntimeException, 包装了Response的类型，和具体错误信息。

ServerErrorException, ClientErrorException 又继承自 WebApplicationException， 分为客户端和服务端错误的异常。

NotFoundException，NotSupportedException 等 又继承自 ClientErrorException， 可以直接使用。

InternalServerErrorException，ServiceUnavailableException等 又继承自 ServerErrorException，可以直接使用。

## rest 异步通信

### polling

在JAX-RS的服务器端，实现异步通信包括两个技术点， 一个是资源方法中对AsyncResponse的使用，另一个是对异步通信的CompletionCallback 和 TimeoutHandler接口的实现。

AsyncResponse会在resume()被调用后 执行回调方法，因此 CompletionCallback 和 TimeoutHandler 要在 resume()被调用之前 进行注册。

同步接口 在一个请求中，对数据库的多次操作，只会在请求结束的时候 commit，

异步 接口，是 每次操作 都会commit。


## Hibernate数据保存操作方法的原理对比

http://fantasyyong.iteye.com/blog/146685

### Hibernate primary key strategy

http://www.blogjava.net/fancydeepin/archive/2012/10/12/hibernate_annotation_pk_strategy.html

### Hibernate 如何映射多张表
```java
private final HibernateBundle<PractiseConfiguration> hibernateBundle = new HibernateBundle<PractiseConfiguration>(Person.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(PractiseConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };
```

上面的代码中 HibernateBundle 的构造期中传入了 Entity类 Person.class, 跟person表对应；
如果还需要映射另外一个表的话，就要在 HibernateBundle 的构造器中传入相应的Entity类，如：

```java
private final HibernateBundle<PractiseConfiguration> hibernateBundle = new HibernateBundle<PractiseConfiguration>(Person.class，People.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(PractiseConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };
```

### retry
```
Retrofit retrofit = new Retrofit.Builder()
                .callFactory(getOkHttpClient())
                .baseUrl(base_url)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        // try the request
                        Response response = chain.proceed(request);

                        int tryCount = 0;
                        while (!response.isSuccessful() && tryCount < 3) {

                            logger.info("reponse code is: " + response.code());
                            logger.info("message is: " + response.message());

                            logger.info("intercept", "Request is not successful - " + tryCount);

                            tryCount++;

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                logger.warn(e.getMessage());
                            }

                            // retry the request
                            response = chain.proceed(request);
                        }

                        // otherwise just pass the original response on
                        return response;
                    }
                })
                .build();
    }
    
   ```


### java8 time date

```java
Instant start = Instant.now();

Instant end = Instant.now();

long duration = Duration.between(start, end).getSeconds();
```

=======
### 调用上传文件接口的方式

#### 用shell脚本 命令如下：

```shell
curl -F file=@/home/16.17.30RC01/test-moon.spd "http://10.62.100.169:28052/oki-ms/oki/upload?access-token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJhZG1pbiIsInJvbGVzIjpbInVzZXIiXSwiZXhwIjoxNTAwMTAwNTUyMzAyfQ.04bU2jTCAwX457vVjo6Npy17c6sxgEgt8EcPTO3UEgE&name=test-moon.spd&version=v1"
```

-F 是上传文件的选项，@后面是spd文件的绝对路径   Url 最好是用双引号 引起来。

#### 在高级语言中调用上传文件接口的方式

1. python 

```python
from poster.encode import multipart_encode
from poster.streaminghttp import register_openers
import urllib2

# resigter this poster handle
register_openers()
            
#encode data
datagen, headers = multipart_encode({"file": open("test-moon.spd", "rb")})
request = urllib2.Request("http://10.62.100.169:28052/oki-ms/oki/upload?access-token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJhZG1pbiIsInJvbGVzIjpbInVzZXIiXSwiZXhwIjoxNTAwMTAwNTUyMzAyfQ.04bU2jTCAwX457vVjo6Npy17c6sxgEgt8EcPTO3UEgE&name=test-moon.spd&version=v1", datagen, headers)
            
#upload file and print rsponse
print urllib2.urlopen(request).read()
            
 ```
 
 ## 同步接口转成异步接口
 
 被同步转异步的接口缠绕了好几天，今天算是搞清楚了，以前一直纠结于Jersey的AsyncResponse类方式的异步，大部分是比较耗时的get请求。
 
 我们目前的实现方式应该是polling，发一个post请求给server，server返回一个任务ID，然后根据任务的ID去查询任务的执行进度。
 
 实际上后台执行任务 只需要起一个线程就可以了，ID返回之后，任务继续执行。跟AsyncResponse没有太大关系，
 
 异步的问题解决之后，后面遇到的问题是在执行任务的线程中 去做数据库操作时要么提示No Hibernate Session bound to thread，要么是不入库，也没有反应就卡着，在资源类里的方法dropwizard提供了一个UnitOfWork注解来解决hibernate的session和事务，但是在飞资源类里的操作数据库的方法只有这个注解还不行， 官方的说法是可以使用UnitOfWorkAwareProxyFactory，纠结了很久终于知道了这个类该如何使用，如下：
 PeopleService peopleService = new UnitOfWorkAwareProxyFactory(hibernateBundle).create(PeopleService.class, PersonDAO.class, dao);
 
 搭配上UnitOfWork注解，数据终于可以正常入库，问题解决！

