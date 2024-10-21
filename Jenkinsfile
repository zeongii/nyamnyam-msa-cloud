pipeline {
    agent any

    environment {
        repository = "zeongiii/nyamnyam-config-server"
        DOCKERHUB_CREDENTIALS = credentials('DockerHub')
        dockerImage = ''
    }

    stages {
        stage('git scm update') {
            steps {
                git url: 'https://github.com/zeongii/nyamnyam-msa-cloud.git', branch: 'main'
            }
        }

        stage('Grant execute permissions') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                dir("./") {
                    sh "./gradlew clean build --stacktrace"
                }
            }
        }

        stage('Build-image') {
            steps {
                script {
                    sh "docker build -t whdcks420/lunch:3.0 ."
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    sh 'docker push whdcks420/lunch:3.0'
                }
            }
        }

        stage('Cleaning up') {
            steps {
                sh "docker rmi whdcks420/lunch:3.0"
            }
        }
    }
}
