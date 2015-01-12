window.layOutDay = (function() {
	var nextId = (function() {
		var curId = 0;
		return function nextId() { return (curId++) + ""; };
	})();

	function doesOverlap(eventA, eventB) {
		// Assumes event.start < event.end is always true.
		// It's easier to figure out whether or not two events don't overlap.  So we figure that
		// out and then invert.
		return !(eventA.end <= eventB.start || eventB.end <= eventA.start);
	}

	function getSiblings(events) {
		var siblings = {};
		events.forEach(function(event) { siblings[event.id] = []; });

		for(var i = 0; i < events.length; i++) {
			var eventA = events[i];

			for(var j = i + 1; j < events.length; j++) {
				var eventB = events[j];

				if(!doesOverlap(eventA, eventB)) { continue; }

				siblings[eventA.id].push(eventB);
				siblings[eventB.id].push(eventA);
			}
		}

		return siblings;
	}

	function dedupCliques(cliques) {
		var hasSeen = {};
		var dedupped = [];

		cliques.forEach(function(clique) {
			var key = clique.sort().join(); // Sorting to normalizes clique representation
			if(!hasSeen[key]) { dedupped.push(clique); }

			hasSeen[key] = clique;
		});

		return dedupped;
	}

	// Finds the largest clique in the connected graph which `root` is a member of.
	function findLargestClique(graph) {
		var visited = {}, nodes = Object.keys(graph);

		// Assume every node is in it's own clique at first
		var maxCliques = nodes.map(function(node) { return [ node ]; });

		var node;
		while(node = nodes.shift()) {
			if(visited[node]) { continue; }

			visited[node] = true;

			var children = graph[node];
			Array.prototype.push.apply(nodes, children);

			var dedup = false;
			maxCliques.forEach(function(clique) {
				var isMember = clique.every(function(id) { return children.indexOf(id) > -1; });

				if(isMember) { clique.push(node); dedup = true; }
			});

			// At the beginning of this function we assumed every node was it's own clique, which
			// is incorrect.  So to help ensure redundnat work isn't done, we're removing duplicate
			// cliques.
			if(dedup) { maxCliques = dedupCliques(maxCliques); }
		}

		maxCliques.sort(function(cliqueA, cliqueB) { return cliqueB.length - cliqueA.length; });

		return maxCliques[0];
	}

	function getConnectedGraphs(siblings) {
		var visited = {}, graphs = [], roots = Object.keys(siblings);

		var root;
		while(root = roots.shift()) {
			if(visited[root]) { continue; }

			var graph = {}, nodes = [ root ];

			var node;
			while(node = nodes.shift()) {
				if(visited[node]) { continue; }

				var children = siblings[node].map(function(event) { return event.id; });
				Array.prototype.push.apply(nodes, children);

				graph[node] = children;
				visited[node] = true;
			}

			graphs.push(graph);
		}

		return graphs;
	}

	// Returns a object that maps event id's to column counts.
	function getColumnCounts(siblings) {
		// A series of overlapping events can be represented as a graph, where each node respresents
		// an event and an edge between two nodes respresents that those two events overlap.
		// When represented in this way, finding the number of columns becomes a matter of finding 
		// the largest clique in that graph:
		// http://en.wikipedia.org/wiki/Clique_%28graph_theory%29#Computer_science

		var columns = {};
		var graphs = getConnectedGraphs(siblings);

		graphs.forEach(function(graph) {
			var clique = findLargestClique(graph);
			var colCount = clique.length;

			Object.keys(graph).forEach(function(id) { columns[id] = colCount; });
		});

		return columns;
	}

	function getIndexes(siblings, columns) {
		var indexes = {};

		var visited = {}, nodes = Object.keys(siblings);

		var node;
		while(node = nodes.shift()) {
			if(visited[node]) { continue; }

			var taken = Array(columns[node]);
			siblings[node].forEach(function(event) {
				if(event.id in indexes) { taken[indexes[event.id]] = true; }
			});

			for(var i = 0; i < taken.length; i++) {
				if(!taken[i]) { indexes[node] = i; break; }
			}
		}

		return indexes;
	}

	function renderEvents(elem, events, minutesShown) {
		var $day = $(elem);

		var siblings = getSiblings(events);
		var columns = getColumnCounts(siblings);
		var indexes = getIndexes(siblings, columns);
		events.forEach(function(event) {
			var colCount = columns[event.id];
			var colIdx = indexes[event.id];

			var $event = $("<div class='event'><h3>Sample Item</h3><p>Sample location</p></div>");
			$event.css({
				top: ((100 * event.start / minutesShown) | 0) + "%",
				left: ((100 * colIdx / colCount) | 0) + "%",
				height: ((100 * (event.end - event.start) / minutesShown) | 0) + "%",
				width: ((100 / colCount) | 0) + "%"
			});

			$day.append($event);
		});
	}

	function renderLegend(elem, startHour, minutesShown) {
		var $legend = $(elem);

		for(var i = 0; i <= minutesShown; i += 30) {
			var hour = startHour + ((i / 60) | 0);
			var mins = i % 60;
			var amPm = (hour < 12 ? "am" : "pm");

			if(mins < 10) { mins = "0" + mins; }
			if(hour > 12) { hour = hour % 12; }

			var $time = $("<li><span><span class='time'/><span class='amPm'/></span></li>");
			$time
				.find(".time").text(hour + ":" + mins).end()
				.find(".amPm").text(amPm)
			;

			$legend.append($time);
		}
	}

	return function layOutDay(events) {
		events = events.map(function(event) { return $.extend({ id: nextId() }, event); });

		var minsIn12Hrs = 12 * 60;
		var startHour = 9;
		renderLegend($(".legend"), startHour, minsIn12Hrs);
		renderEvents($(".schedule"), events, minsIn12Hrs);
	};
})();