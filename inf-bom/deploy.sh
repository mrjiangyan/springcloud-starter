#mvn deploy:deploy-file -DgroupId=com.touchbiz -DartifactId=inf-bom -Dversion=$1 -Dpackaging=pom -Dfile=inf-bom.xml  -Durl=https://packages.aliyun.com/maven/repository/2031284-release-KDf3XN -DrepositoryId=rdc-touchbiz-releases

mvn deploy:deploy-file -DgroupId=com.touchbiz -DartifactId=inf-bom -Dversion=$1 -Dpackaging=pom -Dfile=inf-bom.xml  -Durl=https://packages.aliyun.com/maven/repository/2041383-release-F0cHwt -DrepositoryId=rdc-releases

