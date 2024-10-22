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
                    dir('nyamnyam.kr') {
                        sh 'chmod +x gradlew'
                        sh 'cd nyamnyam.kr/server/config-server && ./gradlew build'
                        sh 'cd nyamnyam.kr/server/eureka-server && ./gradlew build'
                         sh 'cd nyamnyam.kr/server/gateway-server && ./gradlew build'


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
