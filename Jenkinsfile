pipeline {
    agent any
    environment {
        DOCKER = 'sudo docker'
    }

    stages {
        stage('Clone Repository') {
            steps {
                checkout scm
                echo 'Checkout Scm'
            }
        }

        stage('Build image') {
            steps {
                sh 'ls -al'
                dir('docker') {
                    sh 'ls -al'
                    sh 'chmod +x ./gradlew'
                    sh './gradlew build'
                    sh 'docker build -t zeongiii/nyamnyam-config-server .'
                }
                echo 'Build image...'
            }
        }

        stage('Remove Previous image') {
            steps {
                script {
                    try {
                        h 'docker stop nyamnyam-config-server'
                        sh 'docker rm nyamnyam-config-server'
                    } catch (e) {
                        echo 'fail to stop and remove container'
                    }
                }
            }
        }
        stage('Run New image') {
            steps {
                sh 'docker run --name zeongiii/nyamnyam-config-server -d -p 8080:8080 zeongiii/nyamnyam-config-server'
                echo 'Run New member image'
            }
        }
    }
}