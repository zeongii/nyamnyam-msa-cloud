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
                sh 'pwd'


                    dir('nyamnyam.kr/server/config-server') {
                        git branch: 'main', url: 'https://github.com/zeongii/nyamnyam-config-server.git', credentialsId: 'githubToken'
                    }

                    dir ('nyamnyam.kr/server/config-server/src/main/resources/secret-server') {
                        git branch: 'main', url: 'https://github.com/zeongii/nyamnyam-secret-server.git', credentialsId: 'githubToken'

                    }
                }
            }
        }


        stage('Build JAR') {
            steps {
                script {
                    dir('nyamnyam.kr/server/config-server') {
                        sh './gradlew clean build'
                    }
                }
            }
        }

        stage('Run JAR') {
            steps {
                script {
                    dir('nyamnyam.kr/server/config-server/build/libs') {
                        // JAR 파일 실행 (실제 JAR 파일 이름으로 변경)
                        sh 'java -jar <생성된_jar파일명>.jar &'
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
