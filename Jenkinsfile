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

        stage('Build and Test JARs') {
            steps {
                script {
                    dir('nyamnyam.kr') {
                        sh 'chmod +x gradlew' // gradlew에 실행 권한 부여

                        // 각 서버에 대해 gradlew를 실행하고, --warning-mode all 옵션 추가
                        def services = [
                            'server/config-server',
                            'server/eureka-server',
                            'server/gateway-server',
                            'service/admin-service',
                            'service/chat-service',
                            'service/post-service',
                            'service/restaurant-service',
                            'service/user-service'
                        ]

                        for (service in services) {
                            dir(service) {
                                // 빌드 실행 및 경고 모드 활성화
                                sh "../../gradlew clean build --warning-mode all"

                                // 테스트 실행 및 실패 시 처리
                                def testResult = sh(script: "../../gradlew test --warning-mode all", returnStatus: true)
                                if (testResult != 0) {
                                    error "Tests failed for ${service}"
                                } else {
                                    echo "Tests passed for ${service}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
