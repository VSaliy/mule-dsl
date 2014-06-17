require "Rover"

declare Rover.config().as("rover")

declare api("rover.raml")
                        .on("/motion/forward" , ActionType.PUT).then(Rover.go())
                        .on("/motion/backard" , ActionType.PUT).then(Rover.backwards())
                        .on("/motion/left" , ActionType.PUT).then(Rover.left())
                        .on("/motion/right" , ActionType.PUT).then(Rover.right())
                      
