pipeline {
    agent any

    environment {
        repository = "zeongiii/nyamnyam-config-server"
        DOCKERHUB_CREDENTIALS = credentials('DockerHub')
        dockerImage = "whdcks420/lunch:3.0"
    }

    stages {
        stage('git scm update') {
            steps {
                git url: 'https://github.com/zeongii/nyamnyam-msa-cloud.git', branch: 'main'
            }
        }

        stage('Grant execute permissions') {
            steps {
                // gradlew 파일에 실행 권한 부여
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                dir("./") {
                    sh "./gradlew clean build --stacktrace"
                }
            }
        }

        stage('Build-image') {
            steps {
                script {
                    sh "docker build -t ${dockerImage} ."
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    sh "docker push ${dockerImage}"
                }
            }
        }

        stage('Cleaning up') {
            steps {
                sh "docker rmi ${dockerImage}"
            }
        }
    }
}
