pipeline {
      agent any

      stages {
              stage('Hello') {
                      steps {
                              echo "hello world"
                      }
              }
              stage('Good') {
                      steps {
                              sh "chmod +x main.sh"

                              retry(3) {
                                      sh "./main.sh"
                              }

                              echo "good day"
                      }
              }
              stage('Finish') {
                      steps {
                              echo "Finished"
                      }
              }
      }
}