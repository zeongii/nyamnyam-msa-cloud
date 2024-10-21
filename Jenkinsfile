pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'zeongiii'
        DOCKER_IMAGE_PREFIX = 'zeongiii/nyamnyam'
    }

    stage('Check Directory Structure') {
        steps {
            sh 'ls -R' // 모든 하위 디렉토리와 파일을 재귀적으로 출력
        }
    }

    stages {
        stage('Build') {
            steps {
                script {
                    def SERVICES = [
                        'server-config-server',
                        'server-gateway-server',
                        'service-admin-service',
                        'service-chat-service',
                        'service-post-service',
                        'service-restaurant-service',
                        'service-user-service'
                    ]

                    for (service in SERVICES) {
                        sh "cd ${service} && ./gradlew build"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    def SERVICES = [
                        'server-config-server',
                        'server-gateway-server',
                        'service-admin-service',
                        'service-chat-service',
                        'service-post-service',
                        'service-restaurant-service',
                        'service-user-service'
                    ]

                    for (service in SERVICES) {
                        sh "cd ${service} && ./gradlew test"
                    }
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def SERVICES = [
                        'server-config-server',
                        'server-gateway-server',
                        'service-admin-service',
                        'service-chat-service',
                        'service-post-service',
                        'service-restaurant-service',
                        'service-user-service'
                    ]

                    for (service in SERVICES) {
                        // Docker 이미지 빌드 및 푸시
                        sh "cd ${service} && docker build -t ${DOCKER_IMAGE_PREFIX}-${service}:latest ."
                        sh "cd ${service} && docker push ${DOCKER_IMAGE_PREFIX}-${service}:latest"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def SERVICES = [
                        'server-config-server',
                        'server-gateway-server',
                        'service-admin-service',
                        'service-chat-service',
                        'service-post-service',
                        'service-restaurant-service',
                        'service-user-service'
                    ]

                    // 각 서비스에 대한 배포 작업 정의
                    for (service in SERVICES) {
                        sh "kubectl apply -f k8s/${service}/deployment.yaml"
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
