var fx = {};

fx.animate = function(elem, duration, styles) {
    var ani = {};
    var _elem = document.getElementById(elem) || elem;
    var _listeners = { start: [], end: [] };
    var _hardStop = false;
    
    var runAnimation = function(startTime) {
        var baseTimeout = 15;
        var aniTimer;
        
        var runAniHelper = function() {
            var runTime = (new Date()).getTime() - startTime;
            
            if(!ani.isRunning && !_hardStop) runTime = duration;
            
            var percentDone = Math.min(runTime / duration, 1);
            
            for(var s in styles) {
                var curVal = (styles[s].end - styles[s].start) * percentDone + styles[s].start;
                
                _elem.style[s] = curVal + styles[s].unit;
            }
            
            if(runTime >= duration || _hardStop) {
                ani.end(_hardStop);
                _hardStop = false;
                clearInterval(aniTimer);
            }
        };
        
        aniTimer = window.setInterval(runAniHelper, baseTimeout);
    };
    
    ani.isRunning = false;
    
    ani.run = function(falseStart) {
        ani.isRunning = true;
        runAnimation((new Date()).getTime());
        
        if(falseStart) return;
        for(var i = 0; i < _listeners.start.length; i++)
            _listeners.start[i](ani);
    };
    
    ani.end = function(hardStop) {
        ani.isRunning = false;
        _hardStop = hardStop;
        
        if(_hardStop) return;
        for(var i = 0; i < _listeners.end.length; i++)
            _listeners.end[i](ani);
    };
    
    ani.onStart = function(evnt) { _listeners.push(evnt); };
    
    ani.onFinish = function(evnt) { _listeners.push(evnt); };
    
    return ani;
};