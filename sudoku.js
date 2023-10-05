/**
 * @param {character[][]} board
 * @return {void} Do not return anything, modify board in-place instead.
 */
var solveSudoku = function(board) {
    const rows = board.length, cols = board[0].length;

    const candidates = Array(9).fill(0).map(_ =>
        Array(9).fill(0).map(_ => "123456789".split(""))
    );
    for(let row = 0; row < rows; row++) {
        for(let col = 0; col < cols; col++) {
            const cell = board[row][col];

            if(cell == ".") { continue; }

            candidates[row][col] = [ cell ];
            propagate(candidates, row, col, cell);
        }
    }

    const solution = findSolution(candidates)

    // console.log(candidates.map(row =>
    //     row.map(r => r.join().padStart(16)).join("|")
    // ).join("\n" + "-".repeat(160) + "\n"));

    for(let row = 0; row < rows; row++) {
        for(let col = 0; col < cols; col++) {
            board[row][col] = solution[row][col][0]
        }
    }

    return board;
};

function propagate(candidates, updatedRow, updatedCol, val) {
    const boxRow = (updatedRow / 3 | 0) * 3
    const boxCol = (updatedCol / 3 | 0) * 3;

    const toPropagate = [];
    for(let idx = 0; idx < 9; idx++) {
        const cells = [
            [ updatedRow, idx ],
            [ idx, updatedCol ],
            [ boxRow + (idx / 3 | 0), boxCol + idx % 3 ],
        ];

        for(const [ row, col ] of cells) {
            if(row == updatedRow && col == updatedCol) { continue; }

            const opts = candidates[row][col];

            const removed = opts.remove(val);
            if(removed && opts.length === 1) { toPropagate.push([ row, col ]); }
        }
    }

    for(const [ row, col ] of toPropagate) {
        propagate(candidates, row, col, candidates[row][col][0]);
    }
}

Array.prototype.remove = function(value) {
    const index = this.indexOf(value);
    if (index > -1) {
        this.splice(index, 1);
        return true;
    }
    return false;
}

function findSolution(candidates) {
    if(isSolved(candidates)) return candidates;

    const [ row, col, opts ] = firstMultipleOpts(candidates);

    for(const opt of opts) {
        const copy = candidates.map(row => row.map(col => [ ...col ]));

        copy[row][col] = [ opt ];
        propagate(copy, row, col, opt);

        if(isInvalid(copy)) { continue; }

        const solution = findSolution(copy);
        if(solution != null) return solution;
    }
    
    return null;
}

function firstMultipleOpts(candidates) {
    for(let row = 0; row < candidates.length; row++) {
        for(let col = 0; col < candidates[0].length; col++) {
            if (candidates[row][col].length > 1) {
                return [ row, col, candidates[row][col] ];
            }
        }
    }

    throw new Error("????");
}

function isSolved(candidates) {
    for(let row = 0; row < candidates.length; row++) {
        for(let col = 0; col < candidates[0].length; col++) {
            if (candidates[row][col].length !== 1) {
                return false;
            }
        }
    }

    return true;
}

function isInvalid(candidates) {
    for(let row = 0; row < candidates.length; row++) {
        for(let col = 0; col < candidates[0].length; col++) {
            if (candidates[row][col].length === 0) {
                return true;
            }
        }
    }

    return false;
}

solveSudoku([
    [".",".","9","7","4","8",".",".","."],
    ["7",".",".",".",".",".",".",".","."],
    [".","2",".","1",".","9",".",".","."],
    [".",".","7",".",".",".","2","4","."],
    [".","6","4",".","1",".","5","9","."],
    [".","9","8",".",".",".","3",".","."],
    [".",".",".","8",".","3",".","2","."],
    [".",".",".",".",".",".",".",".","6"],
    [".",".",".","2","7","5","9",".","."]
]);
