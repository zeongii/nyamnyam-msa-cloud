pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'zeongiii'
        DOCKER_IMAGE_PREFIX = 'zeongiii/nyamnyam-config-server'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                script {
                    dir('nyamnyam.kr') {
                        checkout scm
                    }
                }
            }
        }

        stage('Git Clone') {
            steps {
                script {
                    dir('nyamnyam.kr/server/config-server') {
                        git branch: 'main', url: 'https://github.com/zeongii/nyamnyam-config-server.git', credentialsId: 'githubToken'
                    }
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    dir('nyamnyam.kr') {
                        sh 'pwd'
                        // Docker 빌드 및 푸시 명령어 추가
                        sh "docker build -t ${DOCKER_IMAGE_PREFIX}/config-server:latest server/config-server"
                        sh "docker push ${DOCKER_IMAGE_PREFIX}/config-server:latest"
                    }
                }
            }
        }
    }
}
