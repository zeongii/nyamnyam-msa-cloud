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
                    dir('nyamnyam.kr.server') {
                        checkout scm
                    }
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    dir('nyamnyam.kr.server.config-server') {
                        sh 'pwd'
                        // Docker 빌드 및 푸시 명령어 추가
                        sh "docker build -t ${DOCKER_IMAGE_PREFIX}/config-server:latest ."
                        sh "docker push ${DOCKER_IMAGE_PREFIX}/config-server:latest"
                    }
                }
            }
        }
    }
}
