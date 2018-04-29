module.exports = {
    apps: [
        {
            name: 'ranking',
            args: [
                "-jar",
                "build/libs/jalgoarena-ranking-2.0.0-SNAPSHOT.jar"
            ],
            script: 'java',
            env: {
                PORT: 5006,
                BOOTSTRAP_SERVERS: 'localhost:9092,localhost:9093,localhost:9094',
                EUREKA_URL: 'http://localhost:5000/eureka/'
            }
        }
    ]
};
