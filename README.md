# ServiceHeartBeat
## Practice app for Services and Custom views.
The app shows:
### Services
* Services
  * Started
  * Bound

The ChronosService can be started or bound. It provides Time Tics and Ping services. I developed it
to work with the Heart Beat custom view. The time tics invalidate the view several times a second and when it
receives a PingRequest, it starts adding Heart Beats (check the custom view) in a periodic manner. 

The services stop when you unbind it.

### Heart Beat Custom View
The Custom control plots a graphic to the screen. It does it point by point when it gets invalidated.
You give data to a cache, or not. Everytime you invalidate it, it will draw a value from a cache and plot it. 
if the cache has no data it puts a 0.0f and plots it. if the cache has data it will plot one number at a time
everytime you invalidate it.

There is a predefined series called a Heart Beat. You add it to the cache with *addHeartBeat* the control 
will plot it point by point. A Heart Beat is a series that is calculated when the control starts. It corresponds
to a attenuated sinusoidal wave. after several Tics it will show a heart beat type of graphic.

Check it out.

## Luis Virueña!
