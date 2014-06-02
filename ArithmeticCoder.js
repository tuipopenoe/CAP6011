// Tui Popenoe
// JS Arithmetic Coder

// Arbitrary Character Probabilities
// TODO: Use an input form to change
prob = {
    'a' : [0.0, 0.4],
    'b' : [0.4, 0.3],
    'c' : [0.7, 0.2],
    '@' : [0.9, 0.1]
}

function encode(string, prob){
    console.log('Encode String: ' + string);
    console.log('Character Probabilities: ' + prob);

    var start = 0;
    var width = 1;
    for(cha in string){
        var d_start = prob.cha[0];
        var d_width = prob.cha[1];

        start += d_start*width;
        console.log(start);
        width *= d_width;
    }

    var encoded_value = getRandomUniform(start, start + width);
    console.log(encoded_value);
    console.log(floatToBinary(encoded_value));

    return encoded_value;
}

// Return a random number between min and max
function getRandomUniform(min, max){
    return Math.random() * (max - min) + min;
}

function floatToBinary(value){
    // TODO: float to binary conversion
}

function decode(num, prob){
    console.log('Decode Number: ');
    console.log(num);
    var string = [];
    var start;
    var width;
    while(true){
        for(var key in prob){
            start = key[0];
            width = key[1];
            if((0 < = (num - start) && ((num -start) < width))){
                num = (num - start) / width;
                console.log(num);
                string.append(key);
                console.log(string);
                break;
            }
        }
        if(symbol == '@'){
            break;
        }
    }
    var decoded;
    for(i in string){
        decoded += i;
    }
    console.log(decoded);
    return decoded;
}