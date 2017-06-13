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
