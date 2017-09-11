

#TO DO
class WordNet
	def initialize (syn_file, hyp_file)
		@synset = File.open(syn_file)
		@hypernym = File.open(hyp_file)
		@graph = hyp_to_hash
		@diction = syn_to_hash
	end


	def hyp_to_hash
		graph = Hash.new
	
		while line = @hypernym.gets
			source = line.split(/\W/)
			if !graph.include?(source[0])
				graph.store(source[0], Array.new)
			end
			
			for i in 1..source.length-1
				graph[source[0]].push(source[i])
			end
		end
		
		return graph
	end
	
	def isnoun(syn_arr) 
	
		syn_arr.each{|x|
			
			temp = false
			
			@diction.values.each { |y|
				if y.include?(x)
					temp = true
				end
			}
			
			if temp == false
				return false
			end
		}
		
		return true
	end
	
	def nouns
		sum = 0
		@diction.values.each { |x|
			sum += x.length
		}
		
		sum
	end
	
	def edges
		sum = 0
		
		@graph.each{|x|
			sum += x[1].length
		}
		
		sum
	end
	
	#From here
	def length(v, w) 
		info_getter(v ,w)[0]
	end
	
	def ancestor(v, w)
		temp = info_getter(v, w)
		(temp[1] != nil)? temp[1]: -1
	end
	
	def info_getter(v, w)
		min = -1
		x = nil
		
		for i in 0.. v.length-1
			for j in 0.. w.length-1
				temp = length_single(v[i], w[j])
				if temp[0] >= 0
					if min > temp[0] || min == -1
						min = temp[0]
						x = temp[1]
					end
				end
			end
		end
		
		[min, x]
	end
	
	def length_single (v, w)
		if @diction[v.to_s] == nil || @diction[w.to_s] == nil
			return [-1, nil]
		end
	
		arr_v = Array.new
		arr_w = Array.new
		level_arr(v, arr_v, 0)
		level_arr(w, arr_w, 0)
		
		min = -1
		sid = nil
		for i in 0..arr_v.length-1
			for j in 0..arr_w.length-1
			
				if !(temp = arr_v[i].select { |x| arr_w[j].include?(x)}).empty?
					if min > i+j || min == -1
						min = i+j
						sid = temp
					end
				end
			end
		end
		
		[min, sid]
	end
	
	def level_arr (v, arr, level) 
		if v != nil
			if !arr[level]
				arr[level] = Array.new
			end
			
			arr[level].push(v.to_i)
			
			if @graph[v.to_s]
				@graph[v.to_s].each { |x|
					level_arr(x.to_i, arr, level + 1)
				}
			end
		end
	end
	#TO HERE

	def syn_to_hash
		num_to_w = Hash.new
	
		while line = @synset.gets
			arr_line = line.split(/,/)
			
			word_arr = arr_line[1].split(/\s/)
			
			word_arr.each{|x|
				if !num_to_w.include?(arr_line[0])
					num_to_w.store(arr_line[0], Array.new)
				end
				
				num_to_w[arr_line[0]].push(x)
			}
		end
		
		num_to_w
	end

	def root(v, w)
		v_key = val_to_key(v)
		w_key = val_to_key(w)
		
		arr = Array.new
		ancestor(v_key, w_key).each { |x|
			arr.concat(@diction[x.to_s])
		}
		
		arr.sort
	end
	
	def val_to_key(v)
		arr = Array.new
		
		@diction.each{ |key, value|
			if value.include?(v)
				arr.push(key)
			end
		}
		
		arr
	end
	

	def outcast(input)
		max = Hash.new

		for i in 0..input.length-1
			sum_sq = 0
			for j in 0..input.length-1
				a = val_to_key(input[i])
				b = val_to_key(input[j])
				
				if i != j
					sq = length(a, b)
					sq *=sq
					sum_sq += sq 
				end
			end
			
			if !max[sum_sq]
				max.store(sum_sq, Array.new)
			end
			
			max[sum_sq].push(input[i])
		end
		
		arr = max.sort_by{|k, v| k}.reverse[0][1]
		str = nil
		
		arr.each{|x|
			if str == nil
				str = x
			else 
				str += " " + x
			end
		}
		
		str
	end

end

if ARGV.length < 3 || ARGV.length >5
  fail "usage: wordnet.rb <synsets file> <hypersets file> <command> <filename>"
end

synsets_file = ARGV[0]
hypernyms_file = ARGV[1]
command = ARGV[2]
fileName = ARGV[3]

commands_with_0_input = %w(edges nouns)
commands_with_1_input = %w(outcast isnoun)
commands_with_2_input = %w(length ancestor)



case command
when "root"
	file = File.open(fileName)
	v = file.gets.strip
	w = file.gets.strip
	file.close
    wordnet = WordNet.new(synsets_file, hypernyms_file) 
    r =  wordnet.send(command,v,w)  
    r.each{|w| print "#{w} "}
    
when *commands_with_2_input 
	file = File.open(fileName)
	v = file.gets.split(/\s/).map(&:to_i)
	w = file.gets.split(/\s/).map(&:to_i)
	file.close
    wordnet = WordNet.new(synsets_file, hypernyms_file)
    puts wordnet.send(command,v,w)  
when *commands_with_1_input 
	file = File.open(fileName)
	nouns = file.gets.split(/\s/)
	file.close
    wordnet = WordNet.new(synsets_file, hypernyms_file)
    puts wordnet.send(command,nouns)
when *commands_with_0_input
	wordnet = WordNet.new(synsets_file, hypernyms_file)
	puts wordnet.send(command)
else
  fail "Invalid command"
end
