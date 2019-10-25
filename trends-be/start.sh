
# Mongo
echo "Waiting for mongo"
# it's stupid but i don't want to waist more time fo checking 
#    if mongo is ready for accepting connections
sleep 20

# APPLICATION
java -jar ./target/trends-1.0.0.jar --spring.config.name=compose
