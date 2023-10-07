const nums = [ 9, 8, 6, 6, 6, 5, 4, 2, 0, 0 ];

function findClosest(nums, target) {
    const options = nums.reduce((acc, val) => {
        acc[val] ??= 0;
        acc[val] += 1;
        return acc;
    }, {});

    let max = 0, digits = null;

    const recurse = (numsA, numsB, fillIdx) => {
        const sum =
            (numsA[0] * 1000 + numsA[1] * 100 + numsA[2] * 10 + numsA[3]) +
            (numsB[0] * 1000 + numsB[1] * 100 + numsB[2] * 10 + numsB[3])
        ;
        if(sum > target) { return; }

        if(fillIdx === 4) {
            if(sum > max) {
                max = sum;
                digits = { numsA: [ ...numsA ], numsB: [ ...numsB ] };
            }
            return;
        }
        for(const [ numA, numB ] of combinations(options)) {
            if(fillIdx === 0) {
                if(numA === 0) continue;
                if(numB === 0) continue;
            }
            numsA[fillIdx] = numA;
            numsB[fillIdx] = numB;

            options[numA] -= 1;
            options[numB] -= 1;

            recurse([ ...numsA ], [ ...numsB ], fillIdx + 1)

            options[numA] += 1;
            options[numB] += 1;
        }
    };
    recurse(Array(4).fill(0), Array(4).fill(0), 0);

    return { digits, sum: max };
}

function* combinations(options) {
    const nums = [];
    for(const [ val, dups ] of Object.entries(options)) {
        for(let count = 0; count < dups; count++) {
            nums.push(parseInt(val));
        }
    }

    for(let idxA = 0; idxA < nums.length; idxA++) {
        for(let idxB = idxA + 1; idxB < nums.length; idxB++) {
            yield [ nums[idxA], nums[idxB] ];
            if(nums[idxB] !== nums[idxA]) { yield [ nums[idxB], nums[idxA] ]; }
        }
    }
}

console.log(findClosest(nums, 10_000));
