// JS Arithmetic Coder


prob = {
    'a' : [0.0, 0.4],
    'b' : [0.4, 0.3],
    'c' : [0.7, 0.2],
    '@' : [0.9, 0.1]
}

function encode = function(string, prob){
    console.log('Encode String: ' + string);
    console.log('Character Probabilities: ' + prob);

    start = 0;
    width = 1;
    for( cha in string){
        d_start = prob.cha[0];
        d_width = prob.cha[1];

        start += d_start*width;
        console.log(start);
        width *= d_width;
    }

}