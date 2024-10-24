pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'zeongiii'
        DOCKER_IMAGE_PREFIX = 'zeongiii/nyamnyam-config-server'
        KUBECONFIG_CREDENTIALS_ID = 'kubeconfig'
        NCP_API_KEY = credentials('ncloud-api-key')
        NCP_SECRET_KEY = credentials('ncloud-secret-key')
        services = "server/config-server,server/eureka-server,server/gateway-server,service/admin-service,service/chat-service,service/post-service,service/restaurant-service,service/user-service"
        DOCKERHUB_CREDENTIALS = credentials('DockerHub')
    }

    stages {
        stage('Checkout SCM') {
            steps {
                script {
                sh 'pwd'
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

                    dir ('nyamnyam.kr/deploy') {
                        git branch: 'main', url: 'https://github.com/zeongii/nyamnyam-deploy.git', credentialsId: 'githubToken'
                    }

                }
            }
        }


        stage('Build JAR') {
                   steps {
                       script {
                           // 각 서버에 대해 gradlew를 실행
                           dir('nyamnyam.kr') {
                               sh 'chmod +x gradlew' // gradlew에 실행 권한 부여

                               // services 환경 변수를 Groovy 리스트로 변환
                               def servicesList = env.services.split(',')


                               servicesList.each { service ->
                                   dir(service) {
                                       // ./gradlew clean bootJar 명령어 실행
                                       sh "../../gradlew clean bootJar"
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
                        sh "cd server/config-server && docker build -t ${DOCKER_CREDENTIALS_ID}/nyamnyam-config-server:latest ."
                    }

                    dir('nyamnyam.kr') {
                        sh "docker-compose build"
                    }
                }
            }
        }


         stage('Login to Docker Hub') {
             steps {
                 sh '''
                 echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin
                 '''
             }
         }



        stage('Docker Push') {
            steps {
                script {

                    def servicesList = env.services.split(',')

                    servicesList.each { service ->
                        def serviceName = service.split('/')[1] // 서비스 이름 추출
                        // 각 서비스의 Docker 이미지를 푸시
                        sh "docker push ${DOCKER_CREDENTIALS_ID}/nyamnyam-${serviceName}:latest"
                    }
                }
            }
        }


        stage('Cleaning up') {
            steps {
                script {
                    // 각 서비스의 이미지 삭제
                    def servicesList = env.services.split(',')
                    servicesList.each { service ->
                        def serviceName = service.split('/')[1] // 서비스 이름 추출
                        sh "docker rmi ${DOCKER_CREDENTIALS_ID}/nyamnyam-${serviceName}:latest" // Clean up the pushed image
                    }
                }
            }
        }

        stage('Deploy to k8s') {
                            steps {
                                script {
                                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                                        // 환경 변수로 API Key와 Secret Key 설정 후 ncp-iam-authenticator에 전달
                                        sh '''
                                        export NCP_ACCESS_KEY=$NCP_API_KEY
                                        export NCP_SECRET_KEY=$NCP_SECRET_KEY
                                        kubectl apply -f nyamnyam.kr/deploy/was/config-server/config-server.yaml --kubeconfig=$KUBECONFIG
                                        kubectl apply -f nyamnyam.kr/deploy/was/eureka-server/eureka-server.yaml --kubeconfig=$KUBECONFIG
                                        kubectl apply -f nyamnyam.kr/deploy/was/gateway-server/gateway-server.yaml --kubeconfig=$KUBECONFIG
                                        kubectl apply -f nyamnyam.kr/deploy/was/admin-service/admin-service.yaml --kubeconfig=$KUBECONFIG
                                        kubectl apply -f nyamnyam.kr/deploy/was/chat-service/chat-service.yaml --kubeconfig=$KUBECONFIG
                                        kubectl apply -f nyamnyam.kr/deploy/was/post-service/post-service.yaml --kubeconfig=$KUBECONFIG
                                        kubectl apply -f nyamnyam.kr/deploy/was/restaurant-service/restaurant-service.yaml --kubeconfig=$KUBECONFIG
                                        kubectl apply -f nyamnyam.kr/deploy/was/user-service/user-service.yaml --kubeconfig=$KUBECONFIG
                                        '''
                                    }
                                }
                            }
        }
    }
}
