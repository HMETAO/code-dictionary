pipeline {
    agent any

    stages {
        stage('pullCode') {
            steps {
                echo 'pullCode'
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '74cf0183-bf63-42bb-aca8-b04fd564a5ae', url: 'git@gitee.com:HMETAO/code-dictionary.git']]])
            }
        }

        stage('buildCode') {
            steps {
                echo 'buildCode'
                sh 'mvn clean package'
            }
        }
        stage('buildDockerImages & pullDockerImages') {
            steps {
                echo 'buildDockerImages'
                sh 'docker build -t registry.cn-hangzhou.aliyuncs.com/hmetao_docker/code-dictionary -f dockerFile .'
                withCredentials([usernamePassword(credentialsId: '213b39c7-770e-4802-afc1-b769eb2dafb1', passwordVariable: 'password', usernameVariable: 'username')]) {
                    // some block
                    sh "docker login --username=${username} --password=${password} registry.cn-hangzhou.aliyuncs.com"
                    sh "docker push registry.cn-hangzhou.aliyuncs.com/hmetao_docker/code-dictionary"
                    echo '镜像上传成功'
                }
            }
        }
         stage('publicObject') {
            steps {
                echo 'publicObject'
                sshPublisher(publishers: [sshPublisherDesc(configName: 'master_server', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: "/opt/jenkins_shell/deploy.sh $port", execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
            }
        }
    }
}
