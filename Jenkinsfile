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

        stage('Check and Build Docker Images') {
            steps {
                script {
                    dir('nyamnyam.kr') {
                        // 각 서비스에 대해 이미지를 빌드할지 체크
                        def servicesList = env.services.split(',')
                        servicesList.each { service ->
                            def imageName = "${DOCKER_IMAGE_PREFIX}/${service.split('/').last()}:latest"

                            // 이미지를 pull하여 존재 여부 확인
                            def imageExists = sh(script: "docker images -q ${imageName}", returnStdout: true).trim()

                            if (!imageExists) {
                                sh "cd ${service} && docker build -t ${imageName} ."
                            } else {
                                echo "Image ${imageName} already exists. Skipping build."
                            }
                        }

                        // docker-compose로 서비스 시작
                        sh "docker-compose up -d"
                    }
                }
            }
        }
    }
}
