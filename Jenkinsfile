pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'zeongiii'
        DOCKER_IMAGE_PREFIX = 'zeongiii/nyamnyam'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                script {
                    dir('nyamnyam.kr.server') {
                        checkout scm
                    }
                }
            }
        }

        stage('Docker Build & Push') {
            cd 'nyamnyam.kr.server.config-server'
            sh 'pwd'


        }

}
