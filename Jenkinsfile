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

        stage('Get JAR Name') {
            steps {
                script {
                    dir('nyamnyam.kr/server/config-server/build/libs') {
                        // JAR 파일 이름 확인
                        def jarFile = sh(script: 'ls -1 *.jar', returnStdout: true).trim()
                        echo "Generated JAR file: ${jarFile}"
                    }
                }
            }
        }

        stage('Run JAR') {
            steps {
                script {
                    dir('nyamnyam.kr/server/config-server/build/libs') {
                        // JAR 파일 실행
                        sh "java -jar ${jarFile} &"
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
