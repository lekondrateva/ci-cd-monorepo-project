pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

                stage('Debug Workspace') {
                    steps {
                        script {
                            sh 'echo "Current workspace is: $PWD"'
                            sh 'ls -la'
                        }
                    }
                }

stage('Build Application') {
    steps {
        script {
            echo "WORKSPACE = ${env.WORKSPACE}"

            // Проверка содержимого рабочей директории Jenkins
            sh "ls -la ${env.WORKSPACE}"

            // Проверка содержимого внутри контейнера Alpine
            sh """
                docker run --rm \
                  -v ${env.WORKSPACE}:/project \
                  -w /project \
                  alpine sh -c "echo '🔍 Содержимое внутри контейнера:' && ls -la /project"
            """

            // Сборка Maven-проекта внутри контейнера
            sh """
                docker run --rm \
                  -v ${env.WORKSPACE}:/project:z \
                  -w /project \
                  maven:3.9.6-eclipse-temurin-17 \
                  mvn clean package -DskipTests
            """
        }
    }
}

        stage('Docker Build & Run') {
            steps {
                dir('app') {
                    sh 'docker build -t myapp:latest .'
                    sh 'docker run -d --name my-app-container --network jenkins-net -p 8080:8080 myapp:latest'
                }
            }
        }

        stage('Run Tests') {
            steps {
                sleep(time: 20, unit: "SECONDS")
                dir('tests') {
                    sh '''
                        echo "Waiting for app to become healthy..."
                        for i in {1..10}; do
                          if curl -s http://my-app-container:8080/actuator/health | grep -q UP; then
                            echo "App is up!"
                            break
                          fi
                          echo "Still waiting..."
                          sleep 3
                        done
                    '''
                    sh '''
                        docker run --rm \
                          --network jenkins-net \
                          -v $(pwd):/tests \
                          -w /tests \
                          maven:3.9.6-eclipse-temurin-17 \
                          mvn test
                    '''
                }
            }
            post {
                always {
                    allure([
                        results: [[path: 'tests/target/allure-results']]
                    ])
                }
            }
        }
    }

    post {
        always {
            sh 'docker stop my-app-container || true && docker rm my-app-container || true'
        }
    }
}
