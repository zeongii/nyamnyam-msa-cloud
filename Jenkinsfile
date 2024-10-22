pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'zeongiii'
        DOCKER_IMAGE_PREFIX = 'zeongiii/nyamnyam-config-server'
        services = "server/config-server,server/eureka-server,server/gateway-server,service/admin-service,service/chat-service,service/post-service,service/restaurant-service,service/user-service"

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

                        // services 환경 변수를 Groovy 리스트로 변환
                        def servicesList = env.services.split(',')

                        // 각 서비스에 대해 Gradle 빌드 수행 (테스트 제외)
                        servicesList.each { service ->
                            dir(service) {
                                sh "../../gradlew clean build --warning-mode all -x test"
                            }
                        }
                    }
                }
            }
        }
        stage('Build Docker Images') {
                    steps {
                        script {
                            dir('nyamnyam.kr') {
                                sh "cd server/config-server && docker build -t zeongiii/nyamnyam-config-server:latest ."
                            }

                            dir('nyamnyam.kr') {
                                sh "docker-compose up --build -d"
                            }
                        }
                    }
        }
    }
}
