import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.PauseResult;
import com.amazonaws.services.s3.transfer.PersistableDownload;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;

public class AWS4Test {

	private static S3 utils = S3.getInstance();
	boolean useV4Signature = true;
	AmazonS3 svc = utils.getS3Client(useV4Signature);
	String prefix = utils.getPrefix();
	static Properties prop = new Properties();

	@BeforeClass
	public void generateFiles(){
		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		filePath = "./data/file.txt";
		utils.createFile(filePath, 256 * 1024);	
	}

	@AfterClass
	public void tearDownAfterClass() throws Exception {
		S3.logger.debug("TeardownAfterClass");
		utils.teradownRetries = 0;
		utils.tearDown(svc);
	}

	@AfterMethod
	public void tearDownAfterMethod() throws Exception {
		S3.logger.debug("TeardownAfterMethod");
		utils.teradownRetries = 0;
		utils.tearDown(svc);
	}

	@BeforeMethod
	public void setUp() throws Exception {
		S3.logger.debug("TeardownBeforeMethod");
		utils.teradownRetries = 0;
		utils.tearDown(svc);
	}

        /*
	@Test(description = "object create w/bad X-Amz-Date, fails!")
	public void testObjectCreateBadamzDateAfterEndAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "99990707T215304Z";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("X-Amz-Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 RequestTimeTooSkewed");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "RequestTimeTooSkewed");
		}
	}
        */
        /*
	@Test(description = "object create w/Date after, fails!")
	public void testObjectCreateBadDateAfterEndAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "Tue, 07 Jul 9999 21:53:04 GMT";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 RequestTimeTooSkewed");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "RequestTimeTooSkewed");
		}
	}
        */
        /*
	@Test(description = "object create w/Date before, fails!")
	public void testObjectCreateBadamzDateBeforeEpochAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "9500707T215304Z";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("X-Amz-Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 SignatureDoesNotMatch");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "SignatureDoesNotMatch");
		}
	}
        */
	@Test(description = "object create w/Date before epoch, fails!")
	public void testObjectCreateBadDateBeforeEpochAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "Tue, 07 Jul 1950 21:53:04 GMT";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Date", value);

		svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
	}
        /*
	@Test(description = "object create w/X-Amz-Date after today, fails!")
	public void testObjectCreateBadAmzDateAfterTodayAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "20300707T215304Z";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("X-Amz-Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 RequestTimeTooSkewed");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "RequestTimeTooSkewed");
		}
	}
        */

	@Test(description = "object create w/Date after today, suceeds!")
	public void testObjectCreateBadDateAfterToday4AWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "Tue, 07 Jul 2030 21:53:04 GMT";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Date", value);

		svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));

	}

        /*
	@Test(description = "object create w/X-Amz-Date before today, fails!")
	public void testObjectCreateBadAmzDateBeforeTodayAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "20100707T215304Z";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("X-Amz-Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 RequestTimeTooSkewed");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "RequestTimeTooSkewed");
		}
	}
        */

	@Test(description = "object create w/Date before today, suceeds!")
	public void testObjectCreateBadDateBeforeToday4AWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "Tue, 07 Jul 2010 21:53:04 GMT";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Date", value);

		svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));

	}

        /*
	@Test(description = "object create w/no X-Amz-Date, fails!")
	public void testObjectCreateBadAmzDateNoneAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("X-Amz-Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 RequestTimeTooSkewed");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "RequestTimeTooSkewed");
		}
	}
        */

	@Test(description = "object create w/no Date, suceeds!")
	public void testObjectCreateBadDateNoneAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Date", value);

		svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));

	}
        /*
	@Test(description = "object create w/unreadable X-Amz-Date, fails!")
	public void testObjectCreateBadamzDateUnreadableAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "\\x07";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("X-Amz-Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 SignatureDoesNotMatch");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "SignatureDoesNotMatch");
		}
	}
        */
        /*
	@Test(description = "object create w/unreadable Date, fails!")
	public void testObjectCreateBadDateUnreadableAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "\\x07";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 RequestTimeTooSkewed");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "RequestTimeTooSkewed");
		}
	}
        */
        /*
	@Test(description = "object create w/empty X-Amz-Date, fails!")
	public void testObjectCreateBadamzDateEmptyAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("X-Amz-Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 SignatureDoesNotMatch");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "SignatureDoesNotMatch");
		}
	}
        */
	@Test(description = "object create w/empty Date, suceeds!")
	public void testObjectCreateBadDateEmptyAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Date", value);

		svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));

	}
        /*
	@Test(description = "object create w/invalid X-Amz-Date, fails!")
	public void testObjectCreateBadamzDateInvalidAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "Bad date";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("X-Amz-Date", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 SignatureDoesNotMatch");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "SignatureDoesNotMatch");
		}
	}
        */
	@Test(description = "object create w/invalid Date, suceeds..lies!!")
	public void testObjectCreateBadDateInvalidAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "Bad date";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Date", value);

		svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));

	}

        /*
	@Test(description = "object create w/no User-Agent, fails!")
	public void testObjectCreateBadUANoneAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("User-Agent", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 SignatureDoesNotMatch");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "SignatureDoesNotMatch");
		}
	}
        */
        /*
	@Test(description = "object create w/unreadable User-Agent, fails!")
	public void testObjectCreateBadUAUnreadableAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "\\x07";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("User-Agent", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 SignatureDoesNotMatch");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "SignatureDoesNotMatch");
		}
	}
        */
        /*
	@Test(description = "object create w/empty User-Agent, fails!")
	public void testObjectCreateBadUAEmptyAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("User-Agent", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 403 SignatureDoesNotMatch");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "SignatureDoesNotMatch");
		}
	}

	@Test(description = "object create w/Invalid Authorization, fails!")
	public void testObjectCreateBadAuthorizationInvalidAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "AWS4-HMAC-SHA256 Credential=HAHAHA";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Authorization", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 400 Bad Request");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "400 Bad Request");
		}
	}

	@Test(description = "object create w/Incorrect Authorization, fails!")
	public void testObjectCreateBadAuthorizationIncorrectAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "AWS4-HMAC-SHA256 Credential=HAHAHA";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Authorization", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 400 Bad Request");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "400 Bad Request");
		}
	}
        */

	@Test(description = "object create w/invalid MD5, fails!")
	public void testObjectCreateBadMd5InvalidGarbageAWS4() {

		String bucket_name = utils.getBucketName();
		String key = "key1";
		String content = "echo lima golf";
		String value = "AWS4 HAHAHA";

		svc.createBucket(new CreateBucketRequest(bucket_name));

		InputStream is = new ByteArrayInputStream(content.getBytes());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		metadata.setHeader("Content-MD5", value);

		try {
			svc.putObject(new PutObjectRequest(bucket_name, key, is, metadata));
                        AssertJUnit.fail("Expected 400 InvalidDigest");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "InvalidDigest");
		}
	}

	@Test(description = "multipart uploads for small to big sizes using LLAPI, succeeds!")
	public void testMultipartUploadMultipleSizesLLAPIAWS4() {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";
		svc.createBucket(new CreateBucketRequest(bucket_name));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 53 * 1024 * 1024);

		CompleteMultipartUploadRequest resp = utils.multipartUploadLLAPI(svc, bucket_name, key, 
				5 * 1024 * 1024, filePath);
		svc.completeMultipartUpload(resp);

		CompleteMultipartUploadRequest resp2 = utils.multipartUploadLLAPI(svc, bucket_name, key,
				5 * 1024 * 1024 + 100 * 1024, filePath);
		svc.completeMultipartUpload(resp2);

		CompleteMultipartUploadRequest resp3 = utils.multipartUploadLLAPI(svc, bucket_name, key,
				5 * 1024 * 1024 + 600 * 1024, filePath);
		svc.completeMultipartUpload(resp3);

		CompleteMultipartUploadRequest resp4 = utils.multipartUploadLLAPI(svc, bucket_name, key,
				10 * 1024 * 1024 + 100 * 1024, filePath);
		svc.completeMultipartUpload(resp4);

		CompleteMultipartUploadRequest resp5 = utils.multipartUploadLLAPI(svc, bucket_name, key,
				10 * 1024 * 1024 + 600 * 1024, filePath);
		svc.completeMultipartUpload(resp5);

		CompleteMultipartUploadRequest resp6 = utils.multipartUploadLLAPI(svc, bucket_name, key, 10 * 1024 * 1024,
				filePath);
		svc.completeMultipartUpload(resp6);
	}

	@Test(description = "multipart uploads for small file using LLAPI, succeeds!")
	public void testMultipartUploadSmallLLAPIAWS4() {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";
		svc.createBucket(new CreateBucketRequest(bucket_name));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		long size = 5 * 1024 * 1024;

		CompleteMultipartUploadRequest resp = utils.multipartUploadLLAPI(svc, bucket_name, key, size, filePath);
		svc.completeMultipartUpload(resp);

	}

	@Test(description = "multipart uploads w/missing part using LLAPI, fails!")
	public void testMultipartUploadIncorrectMissingPartLLAPIAWS4() {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";
		svc.createBucket(new CreateBucketRequest(bucket_name));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 13 * 1024 * 1024);

		List<PartETag> partETags = new ArrayList<PartETag>();

		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucket_name, key);
		InitiateMultipartUploadResult initResponse = svc.initiateMultipartUpload(initRequest);

		File file = new File(filePath);
		long contentLength = file.length();
		long partSize = 5 * 1024 * 1024;

		long filePosition = 1024 * 1024;
		for (int i = 7; filePosition < contentLength; i +=3) {
			partSize = Math.min(partSize, (contentLength - filePosition));
			UploadPartRequest uploadRequest = new UploadPartRequest().withBucketName(bucket_name).withKey(key)
					.withUploadId(initResponse.getUploadId()).withPartNumber(i).withFileOffset(filePosition)
					.withFile(file).withPartSize(partSize);
			UploadPartResult res = svc.uploadPart(uploadRequest);
			res.setPartNumber(999);
			partETags.add((PartETag) res.getPartETag());
			
			filePosition += partSize + 512 * 1024;
		}

		CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucket_name, key,
				initResponse.getUploadId(), (List<com.amazonaws.services.s3.model.PartETag>) partETags);

		try {
			svc.completeMultipartUpload(compRequest);
                        AssertJUnit.fail("Expected 400 InvalidPart");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "InvalidPart");
		}
	}

	@Test(description = "multipart uploads w/non existant upload using LLAPI, fails!")
	public void testAbortMultipartUploadNotFoundLLAPIAWS4() {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";
		svc.createBucket(new CreateBucketRequest(bucket_name));

		try {
			svc.abortMultipartUpload(new AbortMultipartUploadRequest(bucket_name, key, "1"));
                        AssertJUnit.fail("Expected 400 NoSuchUpload");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "NoSuchUpload");
		}
	}

	@Test(description = "multipart uploads abort using LLAPI, succeeds!")
	public void testAbortMultipartUploadLLAPIAWS4() {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";
		svc.createBucket(new CreateBucketRequest(bucket_name));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		long size = 5 * 1024 * 1024;

		CompleteMultipartUploadRequest resp = utils.multipartUploadLLAPI(svc, bucket_name, key, size, filePath);
		svc.abortMultipartUpload(new AbortMultipartUploadRequest(bucket_name, key, resp.getUploadId()));

	}

	@Test(description = "multipart uploads overwrite using LLAPI, succeeds!")
	public void testMultipartUploadOverwriteExistingObjectLLAPIAWS4() {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";
		svc.createBucket(new CreateBucketRequest(bucket_name));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		long size = 5 * 1024 * 1024;

		svc.putObject(bucket_name, key, "foo");

		CompleteMultipartUploadRequest resp = utils.multipartUploadLLAPI(svc, bucket_name, key, size, filePath);
		svc.completeMultipartUpload(resp);

		Assert.assertNotEquals(svc.getObjectAsString(bucket_name, key), "foo");

	}

        /*
	@Test(description = "multipart uploads for a very small file using LLAPI, fails!")
	public void testMultipartUploadFileTooSmallFileLLAPIAWS4() {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";
		svc.createBucket(new CreateBucketRequest(bucket_name));

		String filePath = "./data/sample.txt";
		utils.createFile(filePath, 256 * 1024);
		long size = 5 * 1024 * 1024;

		try {
			CompleteMultipartUploadRequest resp = utils.multipartUploadLLAPI(svc, bucket_name, key, size, filePath);
			svc.completeMultipartUpload(resp);
                        AssertJUnit.fail("Expected 400 EntityTooSmall");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "EntityTooSmall");
		}

	}
        */

	@Test(description = "multipart copy for small file using LLAPI, succeeds!")
	public void testMultipartCopyMultipleSizesLLAPIAWS4() {

		String src_bkt = utils.getBucketName(prefix);
		String dst_bkt = utils.getBucketName(prefix);
		String key = "key1";

		svc.createBucket(new CreateBucketRequest(src_bkt));
		svc.createBucket(new CreateBucketRequest(dst_bkt));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		File file = new File(filePath);

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.length());

		try {
			svc.putObject(new PutObjectRequest(src_bkt, key, file));
		} catch (AmazonServiceException err) {
                  // ALI NOTE: what's the point of this try statement
			
		}

		CompleteMultipartUploadRequest resp = utils.multipartCopyLLAPI(svc, dst_bkt, key, src_bkt, key,
				5 * 1024 * 1024);
		svc.completeMultipartUpload(resp);

		CompleteMultipartUploadRequest resp2 = utils.multipartCopyLLAPI(svc, dst_bkt, key, src_bkt, key,
				5 * 1024 * 1024 + 100 * 1024);
		svc.completeMultipartUpload(resp2);

		CompleteMultipartUploadRequest resp3 = utils.multipartCopyLLAPI(svc, dst_bkt, key, src_bkt, key,
				5 * 1024 * 1024 + 600 * 1024);
		svc.completeMultipartUpload(resp3);

		CompleteMultipartUploadRequest resp4 = utils.multipartCopyLLAPI(svc, dst_bkt, key, src_bkt, key,
				10 * 1024 * 1024 + 100 * 1024);
		svc.completeMultipartUpload(resp4);

		CompleteMultipartUploadRequest resp5 = utils.multipartCopyLLAPI(svc, dst_bkt, key, src_bkt, key,
				10 * 1024 * 1024 + 600 * 1024);
		svc.completeMultipartUpload(resp5);

		CompleteMultipartUploadRequest resp6 = utils.multipartCopyLLAPI(svc, dst_bkt, key, src_bkt, key,
				10 * 1024 * 1024);
		svc.completeMultipartUpload(resp6);

	}

	@Test(description = "Upload of a file using HLAPI, succeeds!")
	public void testUploadFileHLAPIBigFileAWS4() {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";
		svc.createBucket(new CreateBucketRequest(bucket_name));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 53 * 1024 * 1024);

		Upload upl = utils.UploadFileHLAPI(svc, bucket_name, key, filePath);

		Assert.assertEquals(upl.isDone(), true);

	}

        /*
	@Test(description = "Upload of a file to non existant bucket using HLAPI, fails!")
	public void testUploadFileHLAPINonExistantBucketAWS4() {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";

		String filePath = "./data/sample.txt";
		utils.createFile(filePath, 256 * 1024);

		try {
			utils.UploadFileHLAPI(svc, bucket_name, key, filePath);
                        AssertJUnit.fail("Expected 400 NoSuchBucket");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "NoSuchBucket");
		}

	}
        */

	@Test(description = "Multipart Upload for file using HLAPI, succeeds!")
	public void testMultipartUploadHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String bucket_name = utils.getBucketName(prefix);

		svc.createBucket(new CreateBucketRequest(bucket_name));

		String dir = "./data";
		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);

		Transfer upl = utils.multipartUploadHLAPI(svc, bucket_name, null, dir);

		Assert.assertEquals(upl.isDone(), true);

	}

        /*
	@Test(description = "Multipart Upload of a file to nonexistant bucket using HLAPI, fails!")
	public void testMultipartUploadHLAPINonEXistantBucketAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String bucket_name = utils.getBucketName(prefix);

		String dir = "./data";
		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);

		try {
			utils.multipartUploadHLAPI(svc, bucket_name, null, dir);
                        AssertJUnit.fail("Expected 400 NoSuchBucket");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "NoSuchBucket");
		}

	}
        */

	@Test(description = "Multipart Upload of a file with pause and resume using HLAPI, succeeds!")
	public void testMultipartUploadWithPauseAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException, IOException {

		String bucket_name = utils.getBucketName(prefix);

		svc.createBucket(new CreateBucketRequest(bucket_name));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		String key = "key1";

		// sets small upload threshold and upload parts size in order to keep the first
		// part smaller than the whole file. Otherwise, the upload throws an exception
		// when trying to pause it
		TransferManager tm = TransferManagerBuilder.standard().withS3Client(svc)
				.withMultipartUploadThreshold(256 * 1024l).withMinimumUploadPartSize(256 * 1024l).build();
		Upload myUpload = tm.upload(bucket_name, key, new File(filePath));

		// pause upload
		TransferProgress progress = myUpload.getProgress();
		long MB = 5 * 1024 * 1024l;
		while (progress.getBytesTransferred() < MB) {
			Thread.sleep(200);
		}
		if (progress.getBytesTransferred() < progress.getTotalBytesToTransfer()) {
			boolean forceCancel = true;
			PauseResult<PersistableUpload> pauseResult = myUpload.tryPause(forceCancel);
			Assert.assertEquals(pauseResult.getPauseStatus().isPaused(), true);

			// persist PersistableUpload info to a file
			PersistableUpload persistableUpload = pauseResult.getInfoToResume();
			File f = new File("resume-upload");
			if (!f.exists())
				f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			persistableUpload.serialize(fos);
			fos.close();

			// Resume upload
			FileInputStream fis = new FileInputStream(new File("resume-upload"));
			PersistableUpload persistableUpload1 = PersistableTransfer.deserializeFrom(fis);
			tm.resumeUpload(persistableUpload1);
			fis.close();
		}
	}

	@Test(description = "Multipart copy using HLAPI, succeeds!")
	public void testMultipartCopyHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String src_bkt = utils.getBucketName(prefix);
		String dst_bkt = utils.getBucketName(prefix);
		String key = "key1";

		svc.createBucket(new CreateBucketRequest(src_bkt));
		svc.createBucket(new CreateBucketRequest(dst_bkt));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		Upload upl = utils.UploadFileHLAPI(svc, src_bkt, key, filePath);
		Assert.assertEquals(upl.isDone(), true);

		Copy cpy = utils.multipartCopyHLAPI(svc, dst_bkt, key, src_bkt, key);
		Assert.assertEquals(cpy.isDone(), true);
	}

        /*
	@Test(description = "Multipart copy for file with non existant destination bucket using HLAPI, fails!")
	public void testMultipartCopyNoDSTBucketHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String src_bkt = utils.getBucketName(prefix);
		String dst_bkt = utils.getBucketName(prefix);
		String key = "key1";

		svc.createBucket(new CreateBucketRequest(src_bkt));

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		Upload upl = utils.UploadFileHLAPI(svc, src_bkt, key, filePath);
		Assert.assertEquals(upl.isDone(), true);

		try {
			utils.multipartCopyHLAPI(svc, dst_bkt, key, src_bkt, key);
                        AssertJUnit.fail("Expected 400 NoSuchBucket");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "NoSuchBucket");
		}
	}
        */

	@Test(description = "Multipart copy w/non existant source bucket using HLAPI, fails!")
	public void testMultipartCopyNoSRCBucketHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String src_bkt = utils.getBucketName(prefix);
		String dst_bkt = utils.getBucketName(prefix);
		String key = "key1";

		svc.createBucket(new CreateBucketRequest(dst_bkt));

		try {
			utils.multipartCopyHLAPI(svc, dst_bkt, key, src_bkt, key);
                        AssertJUnit.fail("Expected 404 Not Found");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "404 Not Found");
		}
	}

	@Test(description = "Multipart copy w/non existant source key using HLAPI, fails!")
	public void testMultipartCopyNoSRCKeyHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String src_bkt = utils.getBucketName(prefix);
		String dst_bkt = utils.getBucketName(prefix);
		String key = "key1";

		svc.createBucket(new CreateBucketRequest(src_bkt));
		svc.createBucket(new CreateBucketRequest(dst_bkt));

		try {
			utils.multipartCopyHLAPI(svc, dst_bkt, key, src_bkt, key);
                        AssertJUnit.fail("Expected 404 Not Found");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "404 Not Found");
		}
	}

	@Test(description = "Download using HLAPI, suceeds!")
	public void testDownloadHLAPIAWS4() throws AmazonServiceException, AmazonClientException, InterruptedException {

		String bucket_name = utils.getBucketName(prefix);
		svc.createBucket(new CreateBucketRequest(bucket_name));
		String key = "key1";

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		Upload upl = utils.UploadFileHLAPI(svc, bucket_name, key, filePath);
		Assert.assertEquals(upl.isDone(), true);

		Download download = utils.downloadHLAPI(svc, bucket_name, key, new File(filePath));
		Assert.assertEquals(download.isDone(), true);

	}

	@Test(description = "Download from non existant bucket using HLAPI, fails!")
	public void testDownloadNoBucketHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String bucket_name = utils.getBucketName(prefix);
		String key = "key1";
		String filePath = "./data/sample.txt";

		try {
			utils.downloadHLAPI(svc, bucket_name, key, new File(filePath));
                        AssertJUnit.fail("Expected 404 Not Found");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "404 Not Found");
		}

	}

	@Test(description = "Download w/no key using HLAPI, suceeds!")
	public void testDownloadNoKeyHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String bucket_name = utils.getBucketName(prefix);
		svc.createBucket(new CreateBucketRequest(bucket_name));
		String key = "key1";

		String filePath = "./data/sample.txt";

		try {
			utils.downloadHLAPI(svc, bucket_name, key, new File(filePath));
                        AssertJUnit.fail("Expected 404 Not Found");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "404 Not Found");
		}

	}

	@Test(description = "Multipart Download using HLAPI, suceeds!")
	public void testMultipartDownloadHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String bucket_name = utils.getBucketName(prefix);
		svc.createBucket(new CreateBucketRequest(bucket_name));
		String key = "key1";
		String dstDir = "./downloads";

		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		Upload upl = utils.UploadFileHLAPI(svc, bucket_name, key, filePath);
		Assert.assertEquals(upl.isDone(), true);

		MultipleFileDownload download = utils.multipartDownloadHLAPI(svc, bucket_name, key, new File(dstDir));
		Assert.assertEquals(download.isDone(), true);
	}

	@Test(description = "Multipart Download with pause and resume using HLAPI, suceeds!")
	public void testMultipartDownloadWithPauseHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException, IOException {

		String bucket_name = utils.getBucketName(prefix);
		svc.createBucket(new CreateBucketRequest(bucket_name));
		String key = "key1";
		String filePath = "./data/file.mpg";
		utils.createFile(filePath, 23 * 1024 * 1024);
		String destPath = "./data/file2.mpg";

		TransferManager tm = TransferManagerBuilder.standard().withS3Client(svc)
				.withMultipartUploadThreshold(64 * 1024l).withMinimumUploadPartSize(64 * 1024l).build();

		Upload upl = utils.UploadFileHLAPI(svc, bucket_name, key, filePath);
		Assert.assertEquals(upl.isDone(), true);

		Download myDownload = tm.download(bucket_name, key, new File(destPath));

		long MB = 2 * 1024 * 1024;
		TransferProgress progress = myDownload.getProgress();
		while (progress.getBytesTransferred() < MB) {
			Thread.sleep(2000);
		}

		if (progress.getBytesTransferred() < progress.getTotalBytesToTransfer()) {
			// Pause the download and create file to store download info
			PersistableDownload persistableDownload = myDownload.pause();
			File f = new File("resume-download");
			if (!f.exists())
				f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			persistableDownload.serialize(fos);
			fos.close();

			// resume download
			FileInputStream fis = new FileInputStream(new File("resume-download"));
			PersistableDownload persistDownload = PersistableTransfer.deserializeFrom(fis);
			tm.resumeDownload(persistDownload);

			fis.close();
		}
	}

	@Test(description = "Multipart Download from non existant bucket using HLAPI, fails!")
	public void testMultipartDownloadNoBucketHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String bucket_name = utils.getBucketName(prefix);

		String key = "key1";
		String dstDir = "./downloads";

		try {
			utils.multipartDownloadHLAPI(svc, bucket_name, key, new File(dstDir));
                        AssertJUnit.fail("Expected 400 NoSuchBucket");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "NoSuchBucket");
		}
	}

        /*
	@Test(description = "Multipart Download w/no key using HLAPI, fails!")
	public void testMultipartDownloadNoKeyHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		String bucket_name = utils.getBucketName(prefix);
		svc.createBucket(new CreateBucketRequest(bucket_name));
		String key = "key1";
		String dstDir = "./downloads";

		try {
			utils.multipartDownloadHLAPI(svc, bucket_name, key, new File(dstDir));
                        AssertJUnit.fail("Expected 404 Not Found");
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "404 Not Found");
		}
	}
        */

        /*
	@Test(description = "Upload of list of files using HLAPI, suceeds!")
	public void testUploadFileListHLAPIAWS4()
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		try {
			String bucket_name = utils.getBucketName(prefix);
			svc.createBucket(new CreateBucketRequest(bucket_name));
			String key = "key1";

			MultipleFileUpload upl = utils.UploadFileListHLAPI(svc, bucket_name, key);
			Assert.assertEquals(upl.isDone(), true);

			ObjectListing listing = svc.listObjects(bucket_name);
			List<S3ObjectSummary> summaries = listing.getObjectSummaries();
			while (listing.isTruncated()) {
				listing = svc.listNextBatchOfObjects(listing);
				summaries.addAll(listing.getObjectSummaries());
			}
			Assert.assertEquals(summaries.size(), 2);
		} catch (AmazonServiceException err) {
			AssertJUnit.assertEquals(err.getErrorCode(), "400 Bad Request");
		}
	}
        */
}
