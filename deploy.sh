#! /bin/bash

#检查程序是否在运行
function is_exist() {
  pid=$(ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}')
  #如果不存在返回1，存在返回0
  if [ -z "${pid}" ]; then
    return 1
  else
    return 0
  fi
}

function stop_application() {
  #  checkjavapid=$(ps -ef | grep java | grep ${APP_NAME} | grep -v grep | grep -v 'deploy.sh' | awk '{print$2}')
  checkjavapid=$(ps -ef | grep java | grep ${JAR_NAME} | grep -v grep | awk '{print$2}')
  if [[ ! $checkjavapid ]]; then
    echo -e "\rno java process"
    return
  fi

  echo "stop java process"
  times=60
  for e in $(seq 60); do
    sleep 1
    COSTTIME=$(($times - $e))
    checkjavapid=$(ps -ef | grep java | grep ${JAR_NAME} | grep -v grep | grep -v 'deploy.sh' | awk '{print$2}')
    if [[ $checkjavapid ]]; then
      kill -31 $checkjavapid
      echo -e "\r        -- stopping java lasts $(expr $COSTTIME) seconds."
    else
      echo -e "\rjava process has exited"
      break
    fi
  done
  echo ""
}

function start_application() {
  is_exist
  if [ $? -eq "0" ]; then
    echo "${JAR_NAME} is already running. pid=${pid} ."
  else
    echo "JAR_NAME=========="${JAR_NAME}
    echo "JAVA_OUT=========="${JAVA_OUT}
    b=${FILE_NAME}
    nohup java -Xms128m -Xmx256m -Xmn128m -XX:MaxMetaspaceSize=128m -XX:CompressedClassSpaceSize=64m -Xss256k -XX:InitialCodeCacheSize=4m -XX:ReservedCodeCacheSize=60m -XX:MaxDirectMemorySize=16m -jar ${JAR_NAME} --spring.profile.active=prod >${JAVA_OUT}/${b%.*}.log 2>&1 &
    apid=$(ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}')
    echo "started ${FILE_NAME} process. pid=${apid}"
  fi
}

#输出运行状态
status() {
  is_exist
  if [ $? -eq "0" ]; then
    echo "${JAR_NAME} is running. Pid is ${pid}"
  else
    echo "${JAR_NAME} is NOT running."
  fi
}

function start_read_dir() {
  #注意此处这是两个反引号，表示运行系统命令
  # shellcheck disable=SC2045

  for file in $(ls $1); do
    #  for file in $(ls $1); do
    if [ -d $1"/"$file ]; then #注意此处之间一定要加上空格，否则会报错
      start_read_dir $1"/"$file
    else
      #      echo $1"/"$file
      #在此处处理文件即可:文件夹：$1 文件名: $file
      #      FILE_NAME=$(basename $file) #仅文件铭带后缀
      #      echo "FILE_NAME:  "${FILE_NAME}
      #      等价于$file

      #      Jar的全路径
      FILE_NAME=$file
      JAR_NAME=$1"/"$file
      JAVA_OUT=$1
      J=".jar"
      if [[ $FILE_NAME == *$J ]]; then
        echo "==============>"$FILE_NAME
        case "$PARAM" in
        "start")
          #start
          start_application
          ;;
        "stop")
          stop_application
          ;;
        "status")
          status
          ;;
        "restart")
          stop_application
          start_application
          ;;
        *)
          usage
          ;;
        esac
      fi
      #      判断jar包是否启动
    fi
  done
}
#初始化操作
function init() {
  echo "初始化操作中....."
}
#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
"start")
  #start
  PARAM="start"
  start_read_dir $(pwd)
  ;;
"stop")
  PARAM="stop"
  start_read_dir $(pwd)
  ;;
"status")
  PARAM="status"
  start_read_dir $(pwd)
  ;;
"restart")
  PARAM="restart"
  init
  start_read_dir $(pwd)
  ;;
*)
  usage
  ;;
esac

#使用说明，用来提示输入参数
function usage() {
  echo "Usage: sh 执行脚本.sh [start|stop|restart|status]"
  exit 1
}
