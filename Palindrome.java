package org.myorg;
import java.io.IOException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class Palindrome extends Configured implements Tool 
{
	public static void main(String[] args) throws Exception 
	{
		int res = ToolRunner.run(new Palindrome(), args);
		System.exit(res);
	}
	public int run(String[] args) throws Exception
	{
		Job job = Job.getInstance(getConf(),"palindrome");
		job.setJarByClass (this.getClass());
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable>
	{
		IntWritable one = new IntWritable(1);
		Text key = new Text();
		String line, tokens[];
		int len,i = 0;
		public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException
		{
			line = lineText.toString();
			tokens = line.split(" ");
			for (String token : tokens)
			{
				len = token.length();
				for (i=0; i < (len+1)/2; i++)
				{
					if (token.charAt(i)==token.charAt(len-i-1))
					{
						continue;
					}
					else
						break;
				}
				if(i==(len+1)/2)
				{
					key.set(token);
					context.write(key,one);
				}
			}
		}
	}
	public static class Reduce extends Reducer <Text, IntWritable, Text, IntWritable>
	{
		private IntWritable value = new IntWritable();
		int sum;
		public void reduce(Text key, Iterable<IntWritable> counts, Context context) throws IOException, InterruptedException
		{
			sum=0;
			for(IntWritable count:counts)
			{
				sum += count.get();
			}
			value.set(sum);
			context.write(key,value);
		} 
	}
}
