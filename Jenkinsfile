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

                    dir ('nyamnyam.kr/server/config-server/src/main/resources/secret-server') {
                        git branch: 'main', url: 'https://github.com/zeongii/nyamnyam-secret-server.git', credentialsId: 'githubToken'
                    }
                }
            }
        }


        stage('Build JAR') {
            steps {
                script {
                    // 각 서버에 대해 gradlew를 실행
                    dir('nyamnyam.kr') {
                        // gradlew에 실행 권한 부여
                        sh 'chmod +x gradlew'

                        // config-server 빌드
                        dir('server/config-server') {
                            sh '../gradlew clean build' // gradlew를 한 단계 위에서 호출
                        }
                        // eureka-server 빌드
                        dir('server/eureka-server') {
                            sh '../gradlew clean build'
                        }
                        // gateway-server 빌드
                        dir('server/gateway-server') {
                            sh '../gradlew clean build'
                        }
                    }
                }
            }
        }

        stage('Build Other Microservices') {
             steps {
                sh './gradlew -p nyamnyam.kr/service/admin-service build'
                sh './gradlew -p nyamnyam.kr/service/chat-service build'
                sh './gradlew -p nyamnyam.kr/service/post-service build'
                sh './gradlew -p nyamnyam.kr/service/restaurant-service build'
                sh './gradlew -p nyamnyam.kr/service/user-service build'
             }
        }




    }
}
