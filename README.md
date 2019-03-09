# javagalileo [![Build Status](https://travis-ci.org/BluewhaleRobot/javagalileo.svg)](https://travis-ci.org/BluewhaleRobot/javagalileo)

Galileo navigation system Java SDK

## 编译

如果你只是想使用Java SDK可以不用自己编译代码。在本项目的[Release页面](https://github.com/BluewhaleRobot/javagalileo/releases)中包含了已经编译好的jar文件。请根据您的系统进行选择，按照下面的使用说明进行使用。

### Windows

安装 JDK 并保证设置好`JAVA_HOME`环境变量

安装 maven 并保证maven已经添加到`PATH`环境变量中

下载源代码

```powershell
git clone https://github.com/BluewhaleRobot/javagalileo
cd javagalileo
git submodule update --recursive
powershell -ExecutionPolicy Unrestricted ./scripts/package.ps1
```

### Linux

安装JDK 并保证设置好`JAVA_HOME`环境变量

安装maven

安装GalileoSDK, 安装方法[参考这里](https://github.com/bluewhalerobot/GalileoSDK)

下载源码

```bash
git clone https://github.com/BluewhaleRobot/javagalileo
cd javagalileo
git submodule update --recursive
./scripts/package.sh
```

程序编译完成后会在 `javagalileo/target` 文件夹内生成 `javagalileo.jar` 文件。

## 使用

使用方法和一般的java jar文件一样。通过IDE添加到自己的项目依赖中即可。

## 例子

下面是一个获取当前局域网内机器人的简单程序例子。程序会打印出当前局域网内所有机器人的ID

```java
import javagalileo.GalileoSDK;
import javagalileo.models.ServerInfo;

public class Main {

    public static void main(String[] args) {
        GalileoSDK sdk = new GalileoSDK();
        int timecount = 0;
        while (timecount < 10 * 1000){
            ServerInfo[] servers = sdk.GetServersOnline();
            if(servers.length == 0){
                System.out.println("No server found");
            }
            for(ServerInfo server:servers){
               System.out.println("ID: " + server.getID());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecount += 1000;
        }
    }
}
```