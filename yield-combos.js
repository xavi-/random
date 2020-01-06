let base_weight = 45;
let plate_weights = [ 5, 10, 20, 30, 50, 70, 90 ];

function* combinations(arr) {
    let intermediates = [ { combo: [], items: arr } ];

    let next;
    while((next = intermediates.pop())) {
        let { combo, items } = next;

        yield combo;

        if(items.length === 0) { continue; }

        intermediates.push(...items.map((item, idx) => {
            return { combo: combo.concat(item), items: items.slice(idx + 1) };
        }));
    }
}

let weights = {};
for(let combo of combinations(plate_weights)) {
    combo = combo.sort((a, b) => a - b);

    let weight = combo.reduce((a, b) => a + b, 0) + base_weight;
    weights[weight] = weights[weight] || []

    if(weights[weight].includes(combo.join("+"))) { continue; } // no duplicates

    weights[weight].push(combo.join("+"));
}

console.log(weights);

let min = Math.min(...Object.keys(weights));
let max = Math.max(...Object.keys(weights));

console.log(`Range -- min: ${min}, max: ${max}`);
console.log(`Combo count: ${Object.values(weights).flat().length}`);

for(let weight = min; weight <= max; weight += Math.min(...plate_weights)) {
    if(weight in weights) { continue; }

    console.log(`Missing weight: ${weight}`);
}